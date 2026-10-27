/* =====================================================================
   PichangaApp - interacciones comunes
   Sin dependencias: el layout y las vistas funcionan aunque falle el JS.
   ===================================================================== */
(function () {
    'use strict';

    /* ------------------------- Menu de usuario ------------------------- */
    var menu = document.querySelector('[data-menu-usuario]');

    if (menu) {
        var boton = menu.querySelector('.usuario-boton');

        var cerrarMenu = function () {
            menu.classList.remove('abierto');
            if (boton) {
                boton.setAttribute('aria-expanded', 'false');
            }
        };

        if (boton) {
            boton.addEventListener('click', function (evento) {
                evento.stopPropagation();
                var abierto = menu.classList.toggle('abierto');
                boton.setAttribute('aria-expanded', abierto ? 'true' : 'false');
            });
        }

        document.addEventListener('click', function (evento) {
            if (!menu.contains(evento.target)) {
                cerrarMenu();
            }
        });

        document.addEventListener('keydown', function (evento) {
            if (evento.key === 'Escape') {
                cerrarMenu();
            }
        });
    }

    /* --------------------- Navegacion movil ---------------------- */
    var toggle = document.querySelector('[data-barra-toggle]');
    var panel = document.querySelector('[data-barra-panel]');

    if (toggle && panel) {
        var alternarPanel = function (abrir) {
            panel.classList.toggle('abierto', abrir);
            toggle.setAttribute('aria-expanded', abrir ? 'true' : 'false');
            toggle.setAttribute('aria-label', abrir ? 'Cerrar menú' : 'Abrir menú');
            var icono = toggle.querySelector('i');
            if (icono) {
                icono.className = abrir ? 'bi bi-x-lg' : 'bi bi-list';
            }
        };

        toggle.addEventListener('click', function (evento) {
            evento.stopPropagation();
            alternarPanel(!panel.classList.contains('abierto'));
        });

        panel.addEventListener('click', function (evento) {
            if (evento.target.closest('a')) {
                alternarPanel(false);
            }
        });

        document.addEventListener('keydown', function (evento) {
            if (evento.key === 'Escape' && panel.classList.contains('abierto')) {
                alternarPanel(false);
                toggle.focus();
            }
        });

        window.addEventListener('resize', function () {
            if (window.innerWidth > 900) {
                alternarPanel(false);
            }
        });
    }

    /* ------------------- Barra con sombra al desplazar ------------------- */
    var barra = document.querySelector('[data-barra]');

    if (barra) {
        var pintarBarra = function () {
            barra.classList.toggle('barra--compacta', window.scrollY > 8);
        };

        pintarBarra();
        window.addEventListener('scroll', pintarBarra, { passive: true });
    }

    /* --------------------- Revelado de secciones ---------------------- */
    var candidatos = document.querySelectorAll(
        '.seccion > .contenedor > .encabezado-seccion, .rejilla > *, .pasos > .paso, .cta-foto'
    );

    if ('IntersectionObserver' in window && candidatos.length) {
        var observador = new IntersectionObserver(function (entradas) {
            entradas.forEach(function (entrada) {
                if (entrada.isIntersecting) {
                    entrada.target.classList.add('visible');
                    observador.unobserve(entrada.target);
                }
            });
        }, { rootMargin: '0px 0px -8% 0px', threshold: 0.08 });

        Array.prototype.forEach.call(candidatos, function (elemento, indice) {
            elemento.classList.add('revelar');
            elemento.style.transitionDelay = Math.min(indice % 3, 2) * 70 + 'ms';
            observador.observe(elemento);
        });

        /* Red de seguridad: si el observador no dispara (capturas, impresión,
           navegadores antiguos), el contenido nunca queda invisible. */
        window.setTimeout(function () {
            Array.prototype.forEach.call(candidatos, function (elemento) {
                elemento.classList.add('visible');
            });
        }, 1200);
    }

    /* --------------------- Autocierre de alertas ---------------------- */
    var alertas = document.querySelectorAll('[data-auto-cierre]');

    Array.prototype.forEach.call(alertas, function (alerta) {
        var temporizador = null;

        var cerrar = function () {
            alerta.style.transition = 'opacity .25s ease, transform .25s ease';
            alerta.style.opacity = '0';
            alerta.style.transform = 'translateY(-4px)';
            window.setTimeout(function () {
                if (alerta.parentNode) {
                    alerta.parentNode.removeChild(alerta);
                }
            }, 260);
        };

        var programar = function () {
            temporizador = window.setTimeout(cerrar, 6000);
        };

        var pausar = function () {
            if (temporizador) {
                window.clearTimeout(temporizador);
            }
        };

        alerta.addEventListener('mouseenter', pausar);
        alerta.addEventListener('focusin', pausar);
        alerta.addEventListener('mouseleave', programar);
        programar();
    });

    /* -------------------- Confirmacion de acciones -------------------- */
    var formularios = document.querySelectorAll('form[data-confirmar]');

    Array.prototype.forEach.call(formularios, function (formulario) {
        formulario.addEventListener('submit', function (evento) {
            var mensaje = formulario.getAttribute('data-confirmar') || '¿Confirmas esta acción?';
            if (!window.confirm(mensaje)) {
                evento.preventDefault();
            }
        });
    });

    /* ------------------------ Selector de estrellas ------------------- */
    var selectores = document.querySelectorAll('[data-selector-estrellas]');

    Array.prototype.forEach.call(selectores, function (selector) {
        var entradas = selector.querySelectorAll('input[type="radio"]');
        var etiquetas = selector.querySelectorAll('label');

        var pintar = function (valor) {
            Array.prototype.forEach.call(etiquetas, function (etiqueta) {
                var propio = Number(etiqueta.getAttribute('data-valor') || 0);
                etiqueta.classList.toggle('activa', propio <= valor);
            });
        };

        Array.prototype.forEach.call(entradas, function (entrada) {
            entrada.addEventListener('change', function () {
                pintar(Number(entrada.value));
            });
        });

        var marcada = selector.querySelector('input[type="radio"]:checked');
        pintar(marcada ? Number(marcada.value) : 0);
    });
})();
