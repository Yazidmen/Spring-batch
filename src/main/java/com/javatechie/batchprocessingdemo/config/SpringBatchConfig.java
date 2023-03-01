package com.javatechie.batchprocessingdemo.config;

import com.javatechie.batchprocessingdemo.entity.Customer;
import com.javatechie.batchprocessingdemo.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    private CustomerRepository customerRepository;

    @Bean
    public FlatFileItemReader<Customer> reader() {
        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv")); //specifier location du fichier
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1); //définit le nombre de lignes à sauter avant de commencer à lire les données
        itemReader.setLineMapper(lineMapper()); //setLineMapper() définit le mapper de ligne à utiliser pour mapper chaque ligne du fichier CSV en un objet Customer.
        return itemReader;
    }
    private LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(); // DelimitedLineTokenizer qui est utilisé pour diviser chaque ligne en champs en utilisant une virgule comme délimiteur.
        lineTokenizer.setDelimiter(","); //définit la chaîne de caractères utilisée comme délimiteur.
        lineTokenizer.setStrict(false); //La méthode setStrict() définit si le tokenizer doit lever une exception s'il y a une erreur de format dans le fichier CSV. Dans ce cas, nous avons défini false pour ignorer les erreurs de format.
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob"); //définit les noms des colonnes du fichier CSV

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>(); //cet obj qui est utilisé pour mapper chaque champ de la ligne en une propriété de l'objetCustomer`.
        fieldSetMapper.setTargetType(Customer.class); //La méthode setTargetType() définit le type de l'objet  que nous voulons créer à partir des données lues.

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;

    }
    //méthode est utilisée pour créer une instance de CustomerProcessor et la rendre disponible pour l'injection de dépendances dans l'application.
    @Bean
    public CustomerProcessor processor() {
        return new CustomerProcessor();
    }
//cette méthode est utilisée pour créer une instance de RepositoryItemWriter<Customer> et la configurer avec les détails nécessaires pour écrire les données dans le référentiel.
    @Bean
    public RepositoryItemWriter<Customer> writer() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save"); //we are just telling the writer just use my customerepo.save() to write the information of the csv data in db
        return writer;
    }

    //cette méthode est utilisée pour créer une nouvelle étape (step) pour le traitement des données CSV. Elle spécifie le Reader, le Processor, le Writer, la taille des lectures et des écritures, et l'Executor de tâches pour l'étape.
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("csv-step").<Customer, Customer>chunk(10) //Cela signifie que chaque "chunk" (paquet) contient 10 éléments.
                .reader(reader()) // spécifie le Reader à utiliser pour lire les données du fichier csv
                .processor(processor())//spécifie le Processor à utiliser pour traiter les données avant de les écrire dans le référentiel.
                .writer(writer())
                .taskExecutor(taskExecutor())//spécifie l'Executor de tâches à utiliser pour exécuter  les tâches de l'étape en parallèle.
                .build(); //La dernière ligne de code appelle la méthode build() sur le StepBuilder pour construire l'étape (step) et retourne l'objet Step nouvellement créé.
    }

    @Bean

    public Job runJob(){
        return jobBuilderFactory.get("importCustomers").flow(step1()).end().build();
    }
    //Le TaskExecutor permet d'exécuter les tâches Spring Batch en parallèle
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor(); //Crée une instance de la classe SimpleAsyncTaskExecutor, qui est un TaskExecutor qui peut exécuter plusieurs tâches en parallèle.
        asyncTaskExecutor.setConcurrencyLimit(10); //Définit la limite de concurrence à 10, ce qui signifie que le TaskExecutor peut exécuter jusqu'à 10 tâches en même temps.
        return asyncTaskExecutor;
    }

}