package me.itzisonn_.meazy.lexer

object LexerManager {
    fun tokenize(string: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var lineNumber = 1

        var i = 0
        while (i < string.length) {
            val substring = string.substring(i)
            var token: Token? = null

            for (tokenType in TokenTypes.getAll()) {
                if (tokenType.regex == null) continue

                val result = tokenType.regex.matchAt(substring, 0)
                if (result != null) {
                    val end = result.range.last + 1
                    val matched = result.value
                    if (!tokenType.canMatch(matched)) continue

                    if (token == null || token.value.length < matched.length) {
                        token = Token(lineNumber, i, end, tokenType, matched)
                    }
                }
            }

            if (token == null) {
                var errorString = substring.split("\n".toRegex()).dropLastWhile { it.isEmpty() }[0]
                if (errorString.length > 20) errorString = errorString.substring(0, 20) + "..."

                throw UnknownTokenException(lineNumber, errorString)
            }

            i += token.value.length - 1
            if (!token.type.shouldSkip) tokens.add(token)

            lineNumber += token.value.length - token.value.replace("\n", "").length
            i++
        }

        tokens.add(Token(lineNumber, string.length, string.length, TokenTypes.endOfFile, ""))
        return tokens
    }
}