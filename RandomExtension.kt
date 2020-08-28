package com.comicoth.common_jvm.extension
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

fun <T> Random.randomIn(list: List<T>): T {
    val randomIndex = nextInt(from = 0, until = list.size - 1)
    return list[randomIndex]
}

fun <T> Random.randomIn(vararg ts: T): T {
    return randomIn(ts.toList())
}


private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
fun Random.randomString(length: Int): String {
    return (1..length)
            .map { nextInt(0, charPool.size) }
            .map { index -> charPool[index] }
            .joinToString("")
}

inline fun <reified T : Any> makeRandomInstance(): T {
    return makeRandomInstance(T::class) as T
}

@ExperimentalStdlibApi
inline fun <reified T : Any> makeRandomListInstance(minSize: Int = 0, maxSize: Int = 10): List<T> {
    return makeRandomListInstance(typeOf<T>(), minSize = minSize, maxSize = maxSize)
}

private fun makeRandomChar() = random.nextInt(0, 255).toChar()

class NoUsableConstructor : Error()

val random = Random
private fun <T : Any> makePrimitiveOrNull(clazz: KClass<T>) = when (clazz) {
    Boolean::class -> random.nextBoolean()
    Int::class -> random.nextInt()
    Long::class -> random.nextLong()
    Double::class -> random.nextDouble()
    Float::class -> random.nextFloat()
    Char::class -> makeRandomChar()
    String::class -> Random.randomString(20)
    else -> null
}

fun <T : Any> makeRandomListInstance(kType: KType, minSize: Int = 0, maxSize: Int = 10): List<T> {

    val listT = mutableListOf<T?>()
    val count = Random.nextInt(minSize, maxSize + 1)
    val classifier = kType.classifier as KClass<T>
    for (i in 1..count) {
        if (classifier == List::class) {
            val classifierChildKType = kType.arguments[0].type
            val classifierChild = classifierChildKType?.classifier
            if (classifierChild != null) {
                val item = makeRandomListInstance<T>(kType = classifierChildKType) as T
                listT.add(item)
            }
        } else {
            val item = makeRandomInstance(classifier)
            listT.add(item)
        }
    }
    return listT.filterNotNull()
}


fun <T : Any> makeRandomInstance(clazz: KClass<T>, isList: Boolean = false): T? {

    val primitive = makePrimitiveOrNull(clazz)
    if (primitive != null) {
        return primitive as? T?
    }

    val constructors = clazz.constructors
            .sortedBy { it.parameters.size }

    for (constructor in constructors) {
        try {
            val arguments = constructor.parameters
                    .map { it.type }
                    .map {
                        val classOb = it.classifier as KClass<*>
                        return@map if (classOb == List::class) {
                            val kType = it.arguments[0].type
                            val classListArg = kType?.classifier as KClass<*>?
                            if (classListArg != null && kType != null) {
                                makeRandomListInstance<Any>(kType = kType)
                            } else {
                                null
                            }
                        } else {
                            makeRandomInstance(classOb)
                        }
                    }
                    .toTypedArray()

            return constructor.call(*arguments)
        } catch (e: Throwable) {
            print(e)
        }
    }

    throw NoUsableConstructor()
}
