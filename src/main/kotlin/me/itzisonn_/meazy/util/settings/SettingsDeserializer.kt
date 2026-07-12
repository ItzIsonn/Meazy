package me.itzisonn_.meazy.util.settings

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure

object SettingsDeserializer : KSerializer<Settings> {
    override val descriptor = buildClassSerialDescriptor("Settings") {
        element<String>("language")
        element<Boolean>("exception_absent_key")
    }

    override fun serialize(encoder: Encoder, value: Settings) {
        error("Unsupported operation")
    }

    override fun deserialize(decoder: Decoder): Settings {
        return decoder.decodeStructure(descriptor) {
            var id: String? = null
            var exceptionAbsentKey: Boolean? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeStringElement(descriptor, 0)
                    1 -> exceptionAbsentKey = decodeBooleanElement(descriptor, 1)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            if (id == null) error("Settings don't have member id")
            if (exceptionAbsentKey == null) error("Settings don't have member exception_absent_key")

            Settings(id, exceptionAbsentKey)
        }
    }
}