package fr.utc.multeract.server.services;

import com.github.javafaker.Faker;
import fr.utc.multeract.server.models.*;
import fr.utc.multeract.server.repositories.HistoryRepository;
import fr.utc.multeract.server.repositories.StoryStepRepository;
import fr.utc.multeract.server.repositories.UserRepository;
import fr.utc.multeract.server.scripts.HtmlRendererType;
import fr.utc.multeract.server.utils.StringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class BootstrapService {
    private static final Logger logger = LoggerFactory.getLogger(BootstrapService.class);

    @Value("${app.state:production}")
    String state;

    @Autowired
    UserRepository userRepository;

    @Autowired
    HistoryRepository historyRepository;


    @Autowired
    StoryStepRepository historyStepRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    public boolean isBootstrapped() {
        //if user admin exists, then the server is bootstrapped
        return this.userRepository.findByUsername("admin").isPresent();
    }

    public void bootstrap() {
        logger.info("Bootstrapping server");
        if (!this.userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@multeract.fr");
            String password = StringGenerator.random(25);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRoles(Collections.singletonList(UserRole.ADMIN));
            this.userRepository.save(admin);
            logger.warn("Admin user created with password: " + password);
            logger.warn("Please change the password as soon as possible");
        }
        //if debug is enabled, we create faker data
        if (state.equals("debug")) {
            logger.info("Debug mode enabled, creating fake data");
            this.createFakeHistories((User) this.userRepository.findByUsername("admin").orElseThrow());
            //create instance
        }
    }

    protected void createFakeHistories(User user) {
        Faker faker = new Faker();
        for (int j = 0; j < 10; j++) {
            var history = new Story();
            history.setTitle(faker.book().title());
            history.setDescription(faker.lorem().paragraph());
            history.setCreator(user);
            history.setImageUrl(faker.internet().image());
            history.setMaxPlayers(faker.number().numberBetween(2, 4));
            history.setMinPlayers(faker.number().numberBetween(1, 2));
            history.setVisibility(faker.options().option(HistoryVisibility.values()));
            this.historyRepository.save(history);
            history.setEntrypoint(this.createHistoryList(faker, history));
            this.historyRepository.save(history);
        }
    }

    private StoryStep createHistoryList(Faker faker, Story history) {
        List<StoryStep> steps = new ArrayList<>();
        for (int i = 0; i < (faker.random().nextInt(7) +1); i++) {
            StoryStep step = new StoryStep();
            step.setTitle(faker.book().title());
            step.setType(faker.options().option(ChoiceStepType.values()));
            step.setValidatorType(faker.options().option(ChoiceStepValidatorType.values()));
            step.setSlug(faker.internet().slug());
            HtmlPageElement page = new HtmlPageElement();
            page.setContent(faker.lorem().paragraph());
            page.setRendererType(faker.options().option(HtmlRendererType.values()));
            step.setPage(page);
            step.setHistory(history);
            step.setScriptController("console.log('Hello world!'); //This is a fake script (step "+i+")");
            steps.add(step);
        }
        return this.historyStepRepository.saveAll(steps).get(0);
    }
}
