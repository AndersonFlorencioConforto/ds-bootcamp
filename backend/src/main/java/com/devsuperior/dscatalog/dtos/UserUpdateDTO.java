package com.devsuperior.dscatalog.dtos;

import com.devsuperior.dscatalog.services.validation.UserUpdateValid;

import java.time.Instant;


@UserUpdateValid
public class UserUpdateDTO extends UserDTO{
    private static final long serialVersionUID = 1L;

    private Instant updatedAt;

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
