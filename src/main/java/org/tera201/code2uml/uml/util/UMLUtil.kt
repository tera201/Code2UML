package org.tera201.code2uml.uml.util

import org.eclipse.uml2.uml.*

/**
 * Набор вспомогательных методов для работы с элементами UML-модели.
 */
object UMLUtil {

    val lock = Any()
    /**
     * Для квалифицированного имени выдать пакет в модели соответствующий этому имени.
     * Если для какого либо имени в цепочке пакета еще нет, пакет с таким именем создается.
     *
     * @param javaQName квалифицированное имя с разделителями - точками.
     * @return пакет с этим квалифицированным именем
     */
    fun getPackage(p: Package, javaQName: String): Package {

        synchronized(lock) {
            val names = javaQName.split(".").toTypedArray()
            var root: Package = p

            for (name in names) {
                if (root.name == name) continue
                var np = root.getNestedPackage(name)
                if (np == null) np = p.createNestedPackage(name)
                root = np
            }
            return root
        }
    }

    fun getClass(p: Package, name: String): Class {

        synchronized(lock) {
            val owned: NamedElement? = p.getOwnedMember(name)
            if (owned == null) {
                val newClass = p.createOwnedClass(name, false)
                newClass.createOwnedComment().body = "0"
                newClass.createOwnedComment().body = "0"
                return newClass
            } else {
                return owned as Class
            }
        }
    }

    fun getInterface(p: Package, name: String): Interface {

        synchronized(lock) {
            val owned: NamedElement? = p.getOwnedMember(name)
            if (owned == null) {
                val newInterface = p.createOwnedInterface(name)
                newInterface.createOwnedComment().body = "0"
                newInterface.createOwnedComment().body = "0"
                return newInterface
            } else {
                return owned as Interface
            }
        }
    }

    fun getEnum(p: Package, name: String): Enumeration {

        synchronized(lock) {
            val owned: NamedElement? = p.getOwnedMember(name)
            if (owned == null) {
                val newEnumeration = p.createOwnedEnumeration(name)
                newEnumeration.createOwnedComment().body = "0"
                newEnumeration.createOwnedComment().body = "0"
                return newEnumeration
            } else {
                return owned as Enumeration
            }
        }
    }

    fun returnModifier(modifierName: String?): VisibilityKind {
        when (modifierName) {
            "public" -> return VisibilityKind.PUBLIC_LITERAL
            "private" -> return VisibilityKind.PRIVATE_LITERAL
            "protected" -> return VisibilityKind.PROTECTED_LITERAL
        }
        return VisibilityKind.PRIVATE_LITERAL
    }

    /**
     * Для квалифицированного имени выдать тип в модели соответствующий этому имени.
     * Если для какого либо имени в цепочке типа еще нет, тип с таким именем создается.
     *
     * @param javaQName квалифицированное имя с разделителями - точками.
     * @return тип с этим квалифицированным именем
     */
    fun getType(model: Model, javaQName: String): Type? {

        synchronized(lock) {
            val names = javaQName.split(".").toTypedArray()
            var root: Package = model
            var type: Type? = null

            for (name in names) {
                val np = root.getNestedPackage(name)
                if (np != null) {
                    // name - это имя пакета.
                    root = np
                    continue
                }
                type = root.getOwnedType(name)
                if (type != null) // name - это имя типа.
                    break
                type = root.createOwnedType(name, UMLPackage.Literals.ASSOCIATION) // eClass?

                // TODO Продолжить поиск, если квалифицированное имя
                // - это имя типа вложенного в тип.
                // Например, класс вложенный в другой класс.
            }
            return type
        }
    }

    /**
     * Для квалифицированного имени найти в модели тип соответствующий этому имени.
     * Если для какого либо имени в цепочке типа еще нет возвращаетя null.
     *
     * @param javaQName квалифицированное имя с разделителями - точками.
     * @return тип с этим квалифицированным именем или null.
     */
    fun findType(model: Model, javaQName: String): Type? {
        val names = javaQName.split("\\.").toTypedArray()
        var root: Package = model
        var type: Type? = null

        for (name in names) {
            val np = root.getNestedPackage(name)
            if (np != null) {
                // name - это имя пакета.
                root = np
                continue
            }

            // Возможно это имя типа.
            type = root.getOwnedType(name)
            break

            // TODO Продолжить поиск, если квалифицированное имя 
            // - это имя типа вложенного в тип. 
            // Например, класс вложенный в другой класс.
        }
        return type
    }

    /**
     * Для квалифицированного имени выдать пакет в модели соответствующий этому имени.
     * Если для какого либо имени в цепочке пакета еще нет, вернуть null.
     *
     * @param javaQName квалифицированное имя с разделителями - точками.
     * @return пакет с этим квалифицированным именем или null.
     */
    fun findPackage(model: Model, javaQName: String): Package? {
        var root: Package = model
        val names = javaQName.split("\\.").toTypedArray()

        for (name in names) {
            val np = root.getNestedPackage(name) ?: return null
            root = np
        }
        return root
    }

    /**
     * Выдать квалифицированное имя без имени модели.
     *
     * @param named именованный элемент модели.
     * @return короткое квалификированное имя разделяемое точками.
     */
    fun javaName(named: NamedElement): String {
        val model = named.model
        val modelNameSize = model.name.length
        val javaQName = named.qualifiedName.replace("::", ".")
        val shift = if (named !== model) 1 else 0
        return javaQName.substring(modelNameSize + shift)
    }

    /**
     * Выдать последнее имя в квалифицированном имени.
     *
     * @param qName квалифицированное имя.
     * @return последннее имя в цепочке имен.
     */
    fun lastName(qName: String): String {
        val k = qName.lastIndexOf('.')
        return qName.substring(k + 1)
    }
}
