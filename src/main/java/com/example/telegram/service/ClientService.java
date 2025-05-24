package com.example.telegram.service;

import com.example.telegram.entity.Client;
import com.example.telegram.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public Client createClient(String name, String secondName, String chatId) {
        Client client = new Client();
        client.setName(name);
        client.setSecondName(secondName);
        client.setTelegramChatId(chatId);
        return repository.save(client);
    }

    public Optional<Client> findByChatId(String chatId) {
        return repository.findByTelegramChatId(chatId);
    }
}
