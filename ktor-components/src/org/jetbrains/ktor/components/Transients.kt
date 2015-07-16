package org.jetbrains.ktor.components

import java.lang.reflect.*

public abstract class TransientDescriptor(val container: ComponentContainer) : ComponentDescriptor {
    public override fun getValue(): Any = createInstance(container.createResolveContext(this));

    protected abstract fun createInstance(context: ValueResolveContext): Any
}

public class TransientTypeComponentDescriptor(container: ComponentContainer, val klass: Class<*>) : TransientDescriptor(container) {
    override fun getDependencies(context: ValueResolveContext): Collection<Type> {
        return calculateClassDependencies(klass)
    }

    protected override fun createInstance(context: ValueResolveContext): Any {
        val binding = klass.bindToConstructor(context)
        val constructor = binding.constructor
        val arguments = bindArguments(binding.argumentDescriptors)
        val instance = constructor.newInstance(*arguments.toTypedArray())!!
        return instance
    }

    public override fun getRegistrations(): Iterable<Class<*>> {
        return (klass.getInterfaces() + klass).toList()
    }
}