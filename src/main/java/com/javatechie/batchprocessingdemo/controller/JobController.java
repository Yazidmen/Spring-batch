package com.javatechie.batchprocessingdemo.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;

    //cette classe JobController définit un endpoint pour l'API REST qui permet de lancer le travail (job) d'importation de données CSV dans la base de données en appelant la méthode importCsvToDBJob().
    //Cette méthode crée un objet JobParameters contenant des paramètres spécifiques pour le travail, puis lance le travail en utilisant JobLauncher. Si une exception est levée pendant l'exécution du travail, elle est capturée et affichée dans la sortie d'erreur standard.
    @PostMapping("/importCustomers")
    public void importCsvToDBJob() {
        JobParameters jobParameters = new JobParametersBuilder() //Cet objet est utilisé pour transmettre des paramètres spécifiques à l'exécution du job
                .addLong("startAt", System.currentTimeMillis()).toJobParameters();
        try {
            jobLauncher.run(job, jobParameters); //la méthode run de JobLauncher pour lancer l'exécution du travail (job). Cette méthode prend deux paramètres : l'instance de Job à exécuter et les paramètres de travail (jobParameters) à utiliser lors de l'exécutio
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }
}