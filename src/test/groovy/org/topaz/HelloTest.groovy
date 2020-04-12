package org.topaz

class HelloTest extends GroovyTestCase {
    void 'test Topaz should return "Hello, World!"' () {
        assert new Topaz().world == "Hello, World!"
    }
}
