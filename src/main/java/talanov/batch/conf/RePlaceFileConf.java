package talanov.batch.conf;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Configuration
public class RePlaceFileConf {
    private final String path = "/Users/dbryzhatov/Desktop/talanov_batch/src/main/resources/outgoing";

    @Bean
    public Tasklet replaceInputFile(@Value("${input}") Resource resource) {
        return (stepContribution, chunkContext) -> {

            if (!Files.exists(Paths.get(path))) {
                Files.createDirectory(Paths.get(path));
            }

            Files.move(
                    Paths.get(resource.getURI()),
                    Paths.get(path + "/in.csv"),
                    StandardCopyOption.REPLACE_EXISTING
            );

            return RepeatStatus.FINISHED;
        };
    }
}
