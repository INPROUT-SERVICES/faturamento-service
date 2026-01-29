package br.com.inproutservices.faturamento_service.enums;

public enum StatusFaturamento {
    PENDENTE_ASSISTANT, // Chegou do Coordenador
    ID_SOLICITADO,      // Assistant confirmou que pediu o ID
    ID_RECEBIDO,        // Assistant recebeu o número
    ID_RECUSADO,        // Recusado
    FATURADO            // Nota emitida
}