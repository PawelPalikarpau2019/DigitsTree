package my.projects;

import my.projects.components.MainFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(App.class).headless(false).run(args);
        MainFrame mainFrame = context.getBean(MainFrame.class);
        mainFrame.show();
    }
}