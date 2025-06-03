package br.com.recargapay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class MsWalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsWalletApplication.class, args);
	}

}
