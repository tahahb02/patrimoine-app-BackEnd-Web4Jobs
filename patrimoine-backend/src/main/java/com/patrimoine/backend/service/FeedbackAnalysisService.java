package com.patrimoine.backend.service;

import com.patrimoine.backend.entity.DiagnosticEquipement;
import com.patrimoine.backend.entity.Feedback;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class FeedbackAnalysisService {

    private final WebClient ollamaClient;
    private final ObjectMapper objectMapper;
    private final DiagnosticEquipementService diagnosticService;

    public FeedbackAnalysisService(WebClient ollamaClient,
                                   DiagnosticEquipementService diagnosticService) {
        this.ollamaClient = ollamaClient;
        this.objectMapper = new ObjectMapper();
        this.diagnosticService = diagnosticService;
    }


    public void analyzeFeedback(Feedback feedback) {
        try {
            System.out.println("Début de l'analyse du feedback pour l'équipement: " + feedback.getEquipmentId());

            String prompt = buildPrompt(feedback);
            System.out.println("Prompt envoyé à Ollama:\n" + prompt);

            ollamaClient.post()
                    .uri("/api/generate")
                    .bodyValue(Map.of(
                            "model", "llama3",
                            "prompt", prompt,
                            "format", "json",
                            "stream", false
                    ))
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(response -> System.out.println("Réponse reçue d'Ollama: " + response))
                    .flatMap(this::processResponse)
                    .subscribe(
                            result -> {
                                System.out.println("Résultat de l'analyse: " + result);
                                if (result.requiresDiagnostic()) {
                                    createDiagnostic(feedback, result);
                                }
                            },
                            error -> System.err.println("Erreur lors de l'analyse: " + error.getMessage())
                    );
        } catch (Exception e) {
            System.err.println("Erreur globale lors de l'analyse du feedback: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildPrompt(Feedback feedback) {
        return String.format(
                "Analyse ce feedback technique d'équipement et réponds en JSON strict : %n" +
                        "{ %n" +
                        "  \"problemes\": [liste des problèmes techniques identifiés], %n" +
                        "  \"diagnostic_requis\": boolean, %n" +
                        "  \"urgence\": \"FAIBLE/MOYEN/ELEVE\" %n" +
                        "} %n" +
                        "%n" +
                        "Feedback à analyser : %n" +
                        "- Équipement: %s (%s) %n" +
                        "- Satisfaction: %d/5 %n" +
                        "- Problèmes: %s %n" +
                        "- Commentaires: %s %n",
                feedback.getEquipmentName(),
                feedback.getEquipmentId(),
                feedback.getSatisfaction(),
                String.join(", ", feedback.getProblemesTechniques()),
                feedback.getCommentaires()
        );
    }

    private Mono<AnalysisResult> processResponse(String jsonResponse) {
        try {
            JsonNode node = objectMapper.readTree(jsonResponse);
            String responseText = node.path("response").asText();
            JsonNode analysis = objectMapper.readTree(responseText);

            boolean diagnosticRequis = analysis.path("diagnostic_requis").asBoolean();
            String urgence = analysis.path("urgence").asText("FAIBLE");
            String problemes = analysis.path("problemes").toString();

            return Mono.just(new AnalysisResult(diagnosticRequis, urgence, problemes));
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Erreur de traitement de la réponse Ollama", e));
        }
    }

    private void createDiagnostic(Feedback feedback, AnalysisResult result) {
        try {
            DiagnosticEquipement diagnostic = new DiagnosticEquipement();
            diagnostic.setIdEquipement(feedback.getEquipmentId());
            diagnostic.setNomEquipement(feedback.getEquipmentName());
            diagnostic.setCategorie(feedback.getDemande().getCategorieEquipement());
            diagnostic.setVilleCentre(feedback.getVilleCentre());
            diagnostic.setDescriptionProbleme(result.problemes());
            diagnostic.setDegreUrgence(DiagnosticEquipement.DegreUrgence.valueOf(result.urgence()));
            diagnostic.setDateDiagnostic(LocalDateTime.now());
            diagnostic.setBesoinMaintenance(true);
            diagnostic.setMaintenanceEffectuee(false);
            diagnostic.setAutomaticDiagnostic(true); // Marquer comme diagnostic automatique

            diagnosticService.saveDiagnostic(diagnostic);
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du diagnostic: " + e.getMessage());
            e.printStackTrace(); // Ajout pour le débogage
        }
    }

    private record AnalysisResult(boolean requiresDiagnostic, String urgence, String problemes) {}

    public void testOllamaConnection() {
        try {
            String testResponse = ollamaClient.post()
                    .uri("/api/generate")
                    .bodyValue(Map.of(
                            "model", "llama3",
                            "prompt", "Test de connexion",
                            "format", "json",
                            "stream", false
                    ))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Juste pour le test

            System.out.println("Test de connexion Ollama réussi: " + testResponse);
        } catch (Exception e) {
            System.err.println("Échec de la connexion à Ollama: " + e.getMessage());
            e.printStackTrace();
        }
    }
}