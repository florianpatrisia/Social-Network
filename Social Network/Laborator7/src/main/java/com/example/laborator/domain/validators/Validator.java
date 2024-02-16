package com.example.laborator.domain.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}