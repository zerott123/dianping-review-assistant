package com.dianping.assistant

import org.junit.Test
import org.junit.Assert.*

class PromptBuilderTest {

    @Test
    fun `systemPrompt 包含去AI味规则`() {
        val prompt = PromptBuilder.systemPrompt()
        assertTrue(prompt.contains("口语化"))
        assertTrue(prompt.contains("首先"))
        assertTrue(prompt.contains("编造"))
    }

    @Test
    fun `userPrompt 包含用户输入`() {
        val prompt = PromptBuilder.userPrompt(
            input = "牛肉饭，还凑活",
            style = "随性",
            wordCount = "110"
        )
        assertTrue(prompt.contains("牛肉饭，还凑活"))
        assertTrue(prompt.contains("110"))
    }

    @Test
    fun `随性风格映射正确`() {
        val prompt = PromptBuilder.userPrompt("测试", "随性", "50")
        assertTrue(prompt.contains("自然随意"))
    }

    @Test
    fun `客观风格映射正确`() {
        val prompt = PromptBuilder.userPrompt("测试", "客观", "50")
        assertTrue(prompt.contains("客观冷静"))
    }

    @Test
    fun `吃货风格映射正确`() {
        val prompt = PromptBuilder.userPrompt("测试", "吃货", "50")
        assertTrue(prompt.contains("描述细致"))
    }
}
