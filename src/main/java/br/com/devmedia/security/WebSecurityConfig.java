package br.com.devmedia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//configurações relacionado a segurança 
@Configuration // indica para o Spring que essa é uma classe de configuração 
@EnableWebSecurity // essa anotação em conjunto com a herança da WebSecurityConfigurerAdapter traz para a classe umas configurações relacionadas seguranças de aplicações web com Spring Security
// assim como viabiliza a integração com Spring MVC precisa sobrescrever 1 metodo configure()
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	// indicamos ao Spring Security que ele deve fazer com as requisições HTTP direcionadas a aplicação, para isso por parametro o Spring fornece uma instacia de HttpSecurity a partir dela conseguimos realizar essas configurações  
    // ou seja quais requisições devem ser autorizadas, quais devem ter usuario auttenticados, se é necessario que o usuario tenha alguma autorização
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
        //informo que vai configurar como as requisições http devem ser tratadas com relação à segurança 
                authorizeRequests()
                
                
       //qualquer requisição iniciada com /webjars/** (barra qualquer coisa que são os asteristicos) essa requisição deve ser liberada  feito isso execute o projeto agora carrega os CSS   
                    .antMatchers("/webjars/**").permitAll()
                    
       //para provir o controle de acesso as paginas o usuario precisa papel de "editor" para que requisições por ele enviadas para /dados-acesso sejam liberadas
                    //ao tentar acessar clicando outras paginas ela ira para pagina de erro, mas para experiencia do usuario isso não é bom 
                    // para isso vai usar um modulo do thymeleaf que se integra ao Spring Segurity  permitindo fazer essa melhoria adicionando no pom.xml a dependencia thymeleaf-extras-springsecurity4
                    //  podera realizar alguns ajustes no html, ele trata o erro de ao clicar em paginas que usuario não tem permissão declarando o xmlns:sec no html 
                  //  o atributo sec:authorize está dizendo que devem exibidas apenas se o usuario logado estiver o papel especificado em "hasRole", caso ele não tenha a opção não deve ser exibida
                    .antMatchers("/dados-acesso").hasAnyRole("EDITOR")
                    
//para provir o controle de acesso as paginas o usuario precisa "admin" de editor para que requisições por ele enviadas para /lista-usuarios sejam liberadas
                    .antMatchers("/lista-usuarios").hasAnyRole("ADMIN")
                    
         
       //indco que essa configuração é valida para qualquer requisão              
                    .anyRequest()
                    
                    
       //sinaliza que usuario precisa está autenticado, assim que o que esta configurando em authorizeRequests(), .anyRequest() e .authenticated() para acessar qualquer pagina da aplicação o usuario precisa estar autenticado, no entendo apenas com esse código o usuario não consegueria 
                    // acessar a pagina de login nesse momento é preciso infomar que tem uma página de "login criada" a ser utilizada no lugar na página de "login default", indicar tambem que acesso a essa pagina precisa ser independente do usuario estar ou não logado, isso foi feita nessas linhas de codigo 
                    .authenticated()
       
                 
      //indicar que irá realizar outra configuração de segurança
                    .and()
                    
                  
       //sinaliza para o framework que autenticação pode ser feita via formulario de login então com a chama de loginPage  
                .formLogin()
                
                
       //passa como parametro a url para acesso a pagina de "login criada", assim sempre que o usuario não estiver autenticado e tentar acessar algum recurso protegido será direcionado para tela de login 
                    .loginPage("/login")
                    
                    
       //indica que essa página pode ser acessada por todos independente do usuario está autenticado ou não, ao executar aplicação novamente ele vai abrir agora a pagina de "login criada" porém ele não vai carregar as classes de bootstrap observando a pagina de login
       //na tag head tem um link para o css do Bootstrap nesse ponto que está o problema, o Spring Security está barrando esse caminho iniciado com /webJars, só foi liberado o /login no formulario de login 
      //sendo necessario adionar o .antMatchers("/webjars/**").permitAll()
                    .permitAll()
                    
                    
     //indicar que irá realizar outra configuração de segurança
                    .and()
                    
                    
      //observando a classe WebSecurityConfig como ela já herda WebSecurityConfigurerAdapter na prática o logaout está funcionando, no entanto isso não acontecera exatamente como desejado, 
      // por que não? ao clicar no botão sair na URL tem o endereço localhost:8080/login ao inves de /login?logout e a mensagem de usuario desconectado aparece por que isso aconteceu? Por motivo simples como informado anteriormente ao fazer logout o Spring Security redirecioanr o usuario 
      // para /login?logout, problema que está acontecendo esse endereço será barrado pelo framework e consequentemente o usuario sera direcionado para /login qualquer endereço diferente /login fara que o Spring security 
      //para resolver isso adiciona o .logout() o url passa ser /login?logout e parece a mensagem de usuario desconectado
                .logout()
                    .permitAll()
                    
     //indicar que irá realizar outra configuração de segurança 
                    .and()
     // para lembrar o usuario e senha, mesmo indo nos cookies deletando o SessionID o Spring começa uma sessão automaticamente para um usuário quando a funcionalidade de remembre-me estiver habilitada evitando a necessidade de usuário informar login e senha toda vez que acessar aplicação 
                .rememberMe();
    }

	// configurar o usuario e senha em memoria, pode testar o controle de acesso da aplicação, sem para que isso precisa executar operações no banco de dados
	// passando como parametro uma instancia de AuthenticationManagerBuilder, esse metodo inclusive poderia receber outro nome, no entanto é uma sugestão da equipe do Spring na documentação
	// com relação ao parametro como proprio nome indica atraves dele que configuramos o mecanismo de autenticação do Spring Security
    @Autowired //Spring cuidara da execução desse metodo  
    public void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {
        builder
        // como vai definir uma configuração autenticação em memoria chama .inMemoryAuthentication() a partir disso com metodo .withUser definimos o nome do usuario, metodo .password especificamos a senha e metodo .roles indica os papeis que esse usuario tem permissão na aplicação
                .inMemoryAuthentication()
                .withUser("eduardo").password("123456").roles("EDITOR", "ADMIN")
                .and()
                .withUser("fernanda").password("123456").roles("EDITOR");
        // Ao tentar fazer o login e senha com a senha 123456 ele dá erro ficando na página de login, no console ele dá seguinte erro 
        // java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null" essa é uma mudança do Spring Security 5 um de seus objetivos é fazer que aplicação torne mais segura, evitando o armazenamento de senhas sem estar criptografada
        // por isso precisa configurar um passwordEncoder() lembra-se que não interessante salvar a senha do usuario no banco de dados sem ter aplicado por exemplo algum algoritmo de rege
        // declara o codigo para fazer isso no passwordEncoder()
    }
    // Spring será responsavel por gerenciar esse metodo que criptografa a senha
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
