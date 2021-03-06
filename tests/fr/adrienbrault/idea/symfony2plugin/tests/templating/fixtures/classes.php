<?php

namespace
{
    define('CONST_FOO', 'CONST_FOO');
}

namespace Symfony\Component\HttpKernel\Bundle
{
    interface Bundle {}
}

namespace FooBundle
{
    use Symfony\Component\HttpKernel\Bundle\Bundle;
    class FooBundle implements Bundle {}
}

namespace FooBundle\Controller
{
    class FooController
    {
        public function barAction() {}
    }
}

namespace Foo\ConstantBar
{
    class Foo
    {
        const FOO = 'BAR';
    }
}