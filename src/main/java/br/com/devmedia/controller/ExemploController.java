package br.com.devmedia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// será responsavel por atender as requisições do usuario para cada um desses endereços
@Controller
public class ExemploController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/lista-usuarios")
    public String listarUsuarios() {
        return "lista-usuarios";
    }

    @GetMapping("/dados-acesso")
    public String relatorioAcessos() {
        return "dados-acesso";
    }

    // direcionará o usuario para tela de login, essa requisição é do tipo Get, a requisição do tipo Post será gerenciada pelo Spring Security, uma coisa menos para se preocupar,
    // tem que informar para Spring Security que ele deve utilizar a pagina de login criada, criando a classe WebSecurityConfig
    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
