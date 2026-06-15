package me.itzisonn_.meazy.datagen.deserializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import me.itzisonn_.meazy.lexer.TokenTypeSet
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.registry.defaultIdentifier

object TokenTypeSetDeserializer : KSerializer<TokenTypeSet> {
    override val descriptor = buildClassSerialDescriptor("TokenTypeSet") {
        element<String>("id")
        element<Set<String>>("token_types")
    }

    override fun serialize(encoder: Encoder, value: TokenTypeSet) {
        error("Unsupported operation")
    }

    override fun deserialize(decoder: Decoder): TokenTypeSet {
        return decoder.decodeStructure(descriptor) {
            var id: String? = null
            var tokenTypes: Set<String>? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeStringElement(descriptor, 0)
                    1 -> tokenTypes = decodeSerializableElement(descriptor, 1, SetSerializer(String.serializer()))
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            if (id == null) error("TokenTypeSet doesn't have member id")
            if (tokenTypes == null) error("TokenTypeSet doesn't have member token_types")

            TokenTypeSet(id, tokenTypes
                .map { id ->
                    Registries.TOKEN_TYPES.getEntry(defaultIdentifier(id)).value
                }
                .toSet()
            )
        }
    }
}