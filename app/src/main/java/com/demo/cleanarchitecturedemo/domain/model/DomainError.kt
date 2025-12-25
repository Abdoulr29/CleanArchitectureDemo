package com.demo.cleanarchitecturedemo.domain.model


sealed class DomainError : Throwable() {
    object Network : DomainError()
    object Server : DomainError()
    object Unknown : DomainError()
}