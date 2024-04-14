package br.com.devmedia.conf;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// são tudo que precisamos para rodar aplicação web no Tomcat, esssa classe é a responsavel pela execução do projeto, postariormente basta colocar localhost:8080
@SpringBootApplication
public class Exemplo {
	
	public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Exemplo.class);
        app.setDefaultProperties(Collections
          .singletonMap("server.port", "9595"));
        app.run(args);
    }

	/*
	 * public static void main(String[] args) { SpringApplication.run(Exemplo.class,
	 * args); }
	 */

}
