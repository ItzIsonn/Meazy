package me.itzisonn_.meazy.util.datagen.deserializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import me.itzisonn_.meazy.lexer.TokenType
import me.itzisonn_.meazy.lexer.TokenTypes

object TokenTypeDeserializer : KSerializer<TokenType> {
    override val descriptor = buildClassSerialDescriptor("TokenType") {
        element<String>("id")
        element<String>("regex")
        element<Boolean>("should_skip")
        element<Boolean>("expect")
    }

    override fun serialize(encoder: Encoder, value: TokenType) {
        error("Unsupported operation")
    }

    override fun deserialize(decoder: Decoder): TokenType {
        return decoder.decodeStructure(descriptor) {
            var id: String? = null
            var regex: String? = null
            var shouldSkip: Boolean? = null
            var expect: Boolean? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeStringElement(descriptor, 0)
                    1 -> regex = decodeStringElement(descriptor, 1)
                    2 -> shouldSkip = decodeBooleanElement(descriptor, 2)
                    3 -> expect = decodeBooleanElement(descriptor, 3)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            if (id == null) error("TokenType doesn't have member id")

            if (expect == true) {
                check(regex == null && shouldSkip == null) {
                    "Expected token type can't have neither regex nor should_skip members"
                }

                val tokenType = TokenTypes.get(id) ?: error("Expected registered token type with id '$id'")
                return@decodeStructure tokenType
            }

            if (shouldSkip == null) shouldSkip = false
            TokenType(id, regex, shouldSkip)
        }
    }
}