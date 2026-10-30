package com.lauram.aiproxy.service;

import com.lauram.aiproxy.dto.GenerationRequest;
import com.lauram.aiproxy.dto.GenerationResponse;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Real Subject: simulates an expensive AI text generation.
 * It sleeps 1200 ms and returns random predefined text. It knows nothing about limits or quotas.
 */
public class MockAIGenerationService implements AIGenerationService {

    private static final List<String> PREDEFINED_PARAGRAPHS = List.of(
            "El patrón Proxy actúa como un intermediario entre el cliente y el objeto real, controlando el acceso a él sin que el cliente note la diferencia.",
            "La inteligencia artificial generativa permite producir texto, imágenes y código a partir de instrucciones en lenguaje natural.",
            "Limitar la cantidad de solicitudes por minuto protege al servicio de abusos y garantiza un uso justo entre todos los usuarios.",
            "Las cuotas mensuales de tokens permiten ofrecer planes diferenciados: gratuito, profesional y empresarial, cada uno con límites distintos.",
            "En Nariño, la transformación digital abre oportunidades para que estudiantes y emprendedores creen soluciones con impacto regional.",
            "Un buen diseño orientado a objetos separa responsabilidades: cada clase hace una sola cosa y la hace bien.",
            "La carga diferida, el control de acceso y la caché son algunas de las ventajas más conocidas del patrón Proxy.",
            "Encadenar proxies permite agregar capas de control de forma independiente, como un rate limiter seguido de un control de cuotas.",
            "Spring Boot facilita la creación de APIs REST robustas gracias a la inyección de dependencias y la configuración automática.",
            "Un frontend claro debe comunicar el estado del sistema en tiempo real: cuánto consumiste, cuánto te queda y cuándo se reinicia.");

    private static final double CHARACTERS_PER_TOKEN = 4.0;

    private final long simulatedLatencyMs;
    private final TokenEstimator tokenEstimator;

    public MockAIGenerationService(long simulatedLatencyMs, TokenEstimator tokenEstimator) {
        this.simulatedLatencyMs = simulatedLatencyMs;
        this.tokenEstimator = tokenEstimator;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        long start = System.currentTimeMillis();
        simulateProcessing();
        String text = buildRandomText(request.getMaxTokens());
        long promptTokens = tokenEstimator.estimate(request.getPrompt());
        long completionTokens = tokenEstimator.estimate(text);
        return new GenerationResponse(
                text,
                promptTokens,
                completionTokens,
                promptTokens + completionTokens,
                System.currentTimeMillis() - start,
                Instant.now(),
                null);
    }

    private void simulateProcessing() {
        try {
            Thread.sleep(simulatedLatencyMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String buildRandomText(int maxTokens) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double targetFraction = 0.6 + random.nextDouble() * 0.4;
        int targetCharacters = (int) (maxTokens * CHARACTERS_PER_TOKEN * targetFraction);
        StringBuilder builder = new StringBuilder();
        while (builder.length() < targetCharacters) {
            String paragraph = PREDEFINED_PARAGRAPHS.get(random.nextInt(PREDEFINED_PARAGRAPHS.size()));
            if (builder.length() + paragraph.length() > targetCharacters && builder.length() > 0) {
                break;
            }
            builder.append(paragraph).append(' ');
        }
        return builder.toString().trim();
    }
}
