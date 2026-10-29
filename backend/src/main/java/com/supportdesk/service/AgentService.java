package com.supportdesk.service;

import com.supportdesk.entity.Agent;
import com.supportdesk.exception.BadRequestException;
import com.supportdesk.exception.ResourceNotFoundException;
import com.supportdesk.repository.AgentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AgentService {

    private final AgentRepository agentRepository;

    public AgentService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public List<Agent> getAllAgents() {
        return agentRepository.findAll();
    }

    public Agent getAgentById(Long id) {
        return agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent with ID " + id + " not found"));
    }

    @Transactional
    public Agent createAgent(Agent agent) {
        if (agent.getName() == null || agent.getName().trim().isEmpty()) {
            throw new BadRequestException("Agent name cannot be blank");
        }
        if (agent.getEmail() == null || agent.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Agent email cannot be blank");
        }
        if (agentRepository.existsByEmail(agent.getEmail())) {
            throw new BadRequestException("Agent with email " + agent.getEmail() + " already exists");
        }
        return agentRepository.save(agent);
    }
}
