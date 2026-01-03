package com.ozodrukh.llm.api

sealed interface Action {
    data class CreateReminder(val name: String)
}