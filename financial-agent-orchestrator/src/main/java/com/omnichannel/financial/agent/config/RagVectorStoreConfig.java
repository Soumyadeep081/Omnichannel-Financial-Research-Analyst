package com.omnichannel.financial.agent.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for the Vector Store used in the local RAG pipeline.
 * Employs Spring AI's SimpleVectorStore for local evaluation, which can be
 * persisted to disk or replaced with Chroma / PGVector in production.
 */
@Configuration
public class RagVectorStoreConfig {

    private static final Logger log = LoggerFactory.getLogger(RagVectorStoreConfig.class);

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        log.info("Initializing Spring AI SimpleVectorStore with EmbeddingModel: {}", embeddingModel.getClass().getSimpleName());
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    /**
     * Fallback mock embedding model if no OpenAI API key is supplied at boot,
     * ensuring the entire application and RAG pipeline can boot and test cleanly offline.
     */
    @Bean
    @ConditionalOnMissingBean(EmbeddingModel.class)
    public EmbeddingModel fallbackEmbeddingModel() {
        log.warn("No primary EmbeddingModel found. Initializing resilient Deterministic Mock EmbeddingModel for zero-dependency local testing.");
        return new EmbeddingModel() {
            @Override
            public float[] embed(String text) {
                float[] vector = new float[384];
                int hash = text != null ? text.hashCode() : 0;
                for (int i = 0; i < 384; i++) {
                    vector[i] = (float) Math.sin(hash + i);
                }
                return vector;
            }

            @Override
            public float[] embed(Document document) {
                return embed(document != null && document.getText() != null ? document.getText() : "");
            }

            @Override
            public int dimensions() {
                return 384;
            }

            @Override
            public EmbeddingResponse call(EmbeddingRequest request) {
                List<Embedding> embeddings = new ArrayList<>();
                int idx = 0;
                for (String input : request.getInstructions()) {
                    embeddings.add(new Embedding(embed(input), idx++));
                }
                return new EmbeddingResponse(embeddings);
            }
        };
    }
}
