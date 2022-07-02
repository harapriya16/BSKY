package com.stl.bsky.controller;

import com.stl.bsky.common.CaptchaModel;
import com.stl.bsky.common.Constants;
import com.stl.bsky.common.Sha512;
import com.stl.bsky.common.StatusResponse;
import com.stl.bsky.common.Utils;
import com.stl.bsky.entity.mdm.GenCodeEntity;
import com.stl.bsky.entity.uac.UserMaster;
import com.stl.bsky.mapper.UacMapper;
import com.stl.bsky.model.registration.GeneratePaswd;
import com.stl.bsky.model.registration.ValidateCodeDto;
import com.stl.bsky.model.registration.ValidateOtpDto;
import com.stl.bsky.model.uac.TokenRefreshRequest;
import com.stl.bsky.model.uac.TokenRefreshResponse;
import com.stl.bsky.service.*;
import com.stl.bsky.service.mdm.MdmService;
import com.stl.bsky.utility.AppUtils;
import com.stl.bsky.utility.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.stl.bsky.common.Constants.*;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.text.AttributedString;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

@RestController
@Slf4j
@RequestMapping(Constants.BASE_PATH + "/auth")
public class AuthenticationController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    MdmService mdmService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private AuthenticationService authService;

    @Autowired
    private UacMapper uacMapper;

    @Value("${captcha.secret.salt}")
    private String captchaSalt;
    
    @Value("${captcha.login-captcha-time}")
    private Long loginCaptchaValidity;
    

    public static final String CAPTCHA_KEY = "STL_CAPTCHA";
    private static final int CAPTCHA_LENGTH = 5;
    private static final long serialVersionUID = 1512371749422L;

    @GetMapping("/pingMe/{inputString}")
    public String pingApi(@PathVariable String inputString) {
        //throw new EmptyInputException("Input String cannot be left blank.");
        return "Hi " + inputString + ", I am in a running state. You can start your work.";
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> generateToken(@RequestBody AuthenticationRequest auth) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        auth.getUserName(),
//                        auth.getPassword()
//                )
//        );
//
//        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(auth.getUserName());
//
//        final String jwtToken = jwtUtil.generateToken(userDetails);
//        System.out.println("Access Token: " + jwtToken);
//
//        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);
//        return ResponseEntity.ok(new AuthenticationResponse(jwtToken, refreshToken));
//    }

    @PostMapping("/login")
    public ResponseEntity<Object> authenticate(@RequestBody Map<String, Object> authData, HttpServletRequest req) {
        System.out.println("req"+Sha512.decrypt(req.getHeader("captchaKey")));
        CaptchaModel cm= Utils.setCaptchaModelClassfromString(Sha512.decrypt(req.getHeader("captchaKey"))); 
        if(authService.checkCaptchaValidity(req)) {
        String decodedCaptcha = Sha512.decrypt(cm.getCaptchaKey());
        //String expireTime = req.getHeader("captchaKeyObj.expireTime");
        
        String actualCaptcha = decodedCaptcha.substring(0, decodedCaptcha.indexOf("#"));

        if (authData.get("captcha") == null) {
            log.warn("Captcha should not be blank.");
            return new ResponseEntity<>(new StatusResponse(0, "Captcha should not be blank."), HttpStatus.OK);
        }

        if (!authData.get("captcha").equals(actualCaptcha)) {
            log.warn("Invalid Captcha.");
            return new ResponseEntity<>(new StatusResponse(0, "Invalid Captcha"), HttpStatus.OK);

        }

        UserMaster user = authService.postUserCredentials(authData.get("username").toString(), authData.get("password").toString(), authData.get("captcha").toString());
        if (user == null) {
            log.warn("User does not exist!");
            return new ResponseEntity<>(new StatusResponse(0, "User does not exist!"), HttpStatus.OK);
        }

        Map<String, Object> customClaims = new HashMap<>();
        customClaims.put("role", user.getRole().getRoleCode());
        customClaims.put("user", user);

        String accessToken = jwtUtil.generateAccessToken(user, customClaims);
        String refreshToken = jwtUtil.generateRefreshToken(user, customClaims);

        return new ResponseEntity<>(authResponse(accessToken, refreshToken, user), HttpStatus.OK);
        }else {
        	return new ResponseEntity<>(new StatusResponse(0, "Captcha Expired"), HttpStatus.OK);
        }
    }

    private Map<String, Object> authResponse(String accessToken, String refreshToken, UserMaster user) {
        Map<String, Object> authResult = new LinkedHashMap<>();
        if (user != null) {
            authResult.put("user", uacMapper.convertUserMasterToUserResponseDTO(user));
            authResult.put("roleName", user.getRole().getRoleName());
            authResult.put("roleCode", user.getRole().getRoleCode());
//			authResult.put("resourcesList", uacService.getResponseDTOListByRole(user.getRole()));
        }
        if (StringUtils.hasText(accessToken)) {
            authResult.put(STR_TOKEN, accessToken);
        }
        if (StringUtils.hasText(refreshToken)) {
            authResult.put(STR_REFRESH_TOKEN, refreshToken);
        }
        authResult.put(STR_STATUS, STR_STATUS_SUCCESS);
        return authResult;
    }

    @RequestMapping(value = {"/getCaptcha"}, method = RequestMethod.GET)
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response, HttpSession session,
                           @RequestParam(value = "height", required = false) String h,
                           @RequestParam(value = "width", required = false) String w,
                           @RequestParam(value = "status", required = false) String status) throws IOException {
        int cHeight = 45;
        int cWidth = 150;
        if (!StringUtils.isEmpty(h) && !StringUtils.isEmpty(w)) {
            cHeight = Integer.parseInt(h);
            cWidth = Integer.parseInt(w);
        }
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Max-Age", 0);
        response.setContentType("image/jpeg");
        Random r = new Random();
        int f = System.currentTimeMillis() > serialVersionUID ? 1 : 0;
        String token = Long.toString(Math.abs(r.nextLong()), 36);
        String ch = token.substring(0, CAPTCHA_LENGTH);//no of characters in captcha image
        Color[] color = {Color.RED, Color.BLUE,
                new Color(0.6662f, 0.4569f, 0.3232f), Color.BLACK,
                Color.LIGHT_GRAY, Color.YELLOW, Color.LIGHT_GRAY, Color.cyan,
                Color.GREEN, Color.black, Color.DARK_GRAY, Color.MAGENTA};
        BufferedImage image = new BufferedImage(cWidth, cHeight,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setColor(color[4]); // or the background color u want
        graphics2D.fillRect(0, 0, cWidth, cHeight);
        Color c = new Color(0.662f, 0.469f, 0.232f);
        GradientPaint gp = new GradientPaint(30, 30, c, 15, 25, Color.black, true);
        graphics2D.setPaint(gp);
        Font font = new Font("Verdana", Font.CENTER_BASELINE, 20);
        FontMetrics metrics = graphics2D.getFontMetrics(font);
        int x = (cWidth - metrics.stringWidth(ch)) / 2;
        int y = ((cHeight - metrics.getHeight()) / 2) + metrics.getAscent();
        AttributedString as1 = new AttributedString(ch);
        as1.addAttribute(TextAttribute.FONT, font);
        as1.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, 0, CAPTCHA_LENGTH);
        graphics2D.drawString(as1.getIterator(), x, y);
        graphics2D.dispose();
        
        String encCaptcha = Sha512.encrypt(ch + "#" + captchaSalt);
        OutputStream outputStream = response.getOutputStream();
        CaptchaModel captchaModel= new CaptchaModel();
        captchaModel.setCaptchaKey(encCaptcha);
        captchaModel.setExpireTime(new Date().getTime()+loginCaptchaValidity);
        captchaModel.setHostAddr(request.getRemoteAddr());
        response.addHeader("captchaKey", Sha512.encryptClass(captchaModel));
        response.setHeader("Access-Control-Expose-Headers", "captchaKey");
      

        ImageIO.write(image, "jpeg", outputStream);
        outputStream.close();

    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        String username = jwtUtil.getUsernameFromToken(requestRefreshToken);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
	    /*return refreshTokenService.findByToken(requestRefreshToken)
	        .map(refreshTokenService::verifyExpiration)
	        .map(RefreshToken::getUser)
	        .map(user -> {*/
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken, "Bearer"));
        /*
         * }) .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
         * "Refresh token is not in database!"));
         */
    }

    /**
     * created by Sagen
     * GenCode inserting into the mdm
     */
    @Operation(summary = "End-point to create gen code with description")
    @PostMapping("/createGenCode")
    public ResponseEntity<?> createGenCode(@RequestBody GenCodeEntity genCodeEntity) {
        if (genCodeEntity.getParentId() < 0) throw new RuntimeException("Enter the valid parent id!!");
        if (genCodeEntity.getName().isEmpty()) throw new RuntimeException("Enter the valid Gen Code Name !!");
        if (genCodeEntity.getDescription().isEmpty())
            throw new RuntimeException("Enter the valid Gen Code! Description!");
        // if (genCodeDto.getSlNo().isEmpty()) throw new RuntimeException("Enter the valid Gen Code!!");
        return mdmService.createGenCode(genCodeEntity);
    }

    /**
     * created by Sagen
     * get GenCode from the mdm
     */
    @Operation(summary = "End-point to create gen code with description")
    @GetMapping("/getDropDownValue")
    public ResponseEntity<?> getAllGenCodeDetails() {
        return mdmService.getAllGenCodeDetails();
    }


    @Operation(summary = "End-point to create gen code with description")
    @GetMapping("/getGenValuesByParentIdAndActive/{parentId}/{isActive}")
    public ResponseEntity<?> getGenCodesByParentIdAndActive(@PathVariable int parentId, @PathVariable int isActive) {
        return mdmService.getGenCodesByParentIdAndActive(parentId, isActive);
    }
        

   }
