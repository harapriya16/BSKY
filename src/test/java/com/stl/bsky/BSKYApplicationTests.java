package com.stl.bsky;

import com.stl.bsky.entity.uac.UserMaster;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class BSKYApplicationTests {

//	@Test
	public void testPasswordEncryption() {
	    PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
	    SimpleStringPBEConfig config = new SimpleStringPBEConfig();
	    config.setPassword("SiliconTechlab@12345$#"); // encryptor's private key
	    config.setAlgorithm("PBEWithMD5AndDES");
	    config.setKeyObtentionIterations("1000");
	    config.setPoolSize("1");
	    config.setProviderName("SunJCE");
	    config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
	    config.setStringOutputType("base64");
	    encryptor.setConfig(config);
	    String plainText = "jdbc:postgresql://localhost:5433/i4c";
	    String encryptedPassword = encryptor.encrypt(plainText);
	    System.out.println("encryptedPassword : " + encryptedPassword);
	}

//	@Test
	public void getCurrentTimeInMillis() {
		System.out.println(new Date(System.currentTimeMillis() + (60 * 1000)));
	}

//	@Test
//	public void testResourcePayment() {
//		ExamMaster examMaster = new ExamMaster();
//		examMaster.setId(1L);
//		ExamCenterRegistration examCenter = new ExamCenterRegistration();
//		examCenter.setId(1L);
//
//		ResourcePayment resourcePayment = new ResourcePayment();
//		resourcePayment.setExamMaster(examMaster);
//		resourcePayment.setExamCenter(examCenter);
//		resourcePayment.setExamDate(new Date());
//		resourcePayment.setExaminationShift("1st Shift");
//		resourcePayment.setManagementId(1L);
//		resourcePayment.setTotalBillAmount(100.00);
//
//		List<PaymentDetails> paymentDetailsList = new ArrayList<>();
//		PaymentDetails paymentDetails1 = new PaymentDetails();
//		ExpenseNature expenseNature = new ExpenseNature();
//		expenseNature.setId(1);
//		paymentDetails1.setExpenseNature(expenseNature);
//		paymentDetails1.setExpenseDate(new Date());
//		paymentDetails1.setExpenseAmount(100.00);
//		paymentDetailsList.add(paymentDetails1);
//
//		resourcePayment.setPaymentDetailsEntityList(paymentDetailsList);
//
//		UserMaster userMaster = new UserMaster();
//		userMaster.setRegistrationId("12");
//		userMaster.setUserName("1111111");
//		paymentService.claimExpenses(resourcePayment, userMaster);
//
//	}

}
