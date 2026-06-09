/**
 * =========================================================================
 * CHERNOBYL EXCLUSION EXPLORER - ARCHIVO JAVASCRIPT PRINCIPAL
 * =========================================================================
 * * ESTRUCTURA DEL ARCHIVO:
 * BLOQUE 1: Configuración Global y Enrutador de Componentes (Navbar/Footer)
 * BLOQUE 2: Utilidades y API Externas (Idiomas, Meteorología)
 * BLOQUE 3: Zona Pública y Formularios (Login, Registro, VIP, Paquetes)
 * BLOQUE 4: Área del Cliente (Reservas, Pasarela de Pago de 3 pasos, Perfil SBU)
 * BLOQUE 5: Intranet SBU (Panel de Administración y Empleados)
 * BLOQUE 5B: Recursos Humanos (Alta Empleados)
 */



document.addEventListener("DOMContentLoaded", () => {

    document.head.insertAdjacentHTML('beforeend', `
	        <link rel="icon" type="image/x-icon" href="/img/favicon_io/favicon.ico">
	        <link rel="icon" type="image/png" sizes="32x32" href="/img/favicon_io/favicon-32x32.png">
	        <link rel="icon" type="image/png" sizes="16x16" href="/img/favicon_io/favicon-16x16.png">
	    `);

    // =========================================================================
    // BLOQUE 1: CONFIGURACIÓN GLOBAL Y ENRUTADOR
    // =========================================================================
    const cargarComponente = (idContenedor, ruta, callback) => {
        const contenedor = document.getElementById(idContenedor);
        if (contenedor) {
            fetch(ruta).then(response => response.text()).then(html => {
                contenedor.innerHTML = html;
                if (callback) callback();
            }).catch(err => console.error("Error cargando componente:", err));
        }
    };

    const esIntranet = window.location.pathname.includes('/intranet/');
    const rutaNavbar = esIntranet ? '/intranet/navbar-admin.html' : '/componentes/navbar.html';
    const rutaFooter = esIntranet ? '/intranet/footer-admin.html' : '/componentes/footer.html';

    cargarComponente('navbar-container', rutaNavbar, () => {
        verificarEstadoSesion();
        if (!esIntranet) {
            actualizarTiempo();
            setInterval(actualizarTiempo, 300000);
        }
        const rutaActual = window.location.pathname.split('/').pop() || 'index.html';
        document.querySelectorAll('.nav-link').forEach(enlace => {
            if (enlace.getAttribute('href').includes(rutaActual)) enlace.classList.add('active');
        });
    });

    cargarComponente('footer-container', rutaFooter, () => {
        if (!esIntranet) inicializarIdiomas();
    });

    // =========================================================================
    // BLOQUE 2: UTILIDADES (Sesión, Idiomas y Meteorología)
    // =========================================================================
    const idiomasInternacionales = {
        "es": "Spanish (ES)", "en": "English (EN)", "de": "German (DE)", "fr": "French (FR)",
        "uk": "Ukrainian (UK)", "zh-CN": "Chinese (Simplified) (ZH)", "hi": "Hindi (HI)",
        "ar": "Arabic (AR)", "ru": "Russian (RU)", "ja": "Japanese (JA)", "it": "Italiano (IT)",
        "pt": "Portuguese (PT)", "nl": "Dutch (NL)", "pl": "Polish (PL)", "tr": "Turkish (TR)"
    };

    function inicializarIdiomas() {
        const selector = document.getElementById("idiomaSelector");
        if (!selector) return;
        selector.innerHTML = '';
        Object.entries(idiomasInternacionales).forEach(([codigo, nombreInEnglish]) => {
            const opt = document.createElement("option");
            opt.value = codigo;
            opt.textContent = nombreInEnglish;
            if (codigo === "es") opt.selected = true;
            selector.appendChild(opt);
        });
        selector.addEventListener("change", (e) => {
            const googleCombo = document.querySelector(".goog-te-combo");
            if (googleCombo) {
                googleCombo.value = e.target.value;
                googleCombo.dispatchEvent(new Event('change'));
            }
        });
    }

    function actualizarTiempo() {
        fetch('/monitor/tiempo-pripyat')
            .then(response => {
                if (!response.ok) throw new Error('Error HTTP');
                return response.json();
            })
            .then(data => {
                const elementoTiempo = document.getElementById('monitor-tiempo');
                if (elementoTiempo) {
                    if (data.iconoCode === "error" || data.descripcion === "No disponible") throw new Error('API Falló');
                    let iconoClase = "fa-cloud";
                    if (data.iconoCode.includes("01")) iconoClase = "fa-sun";
                    else if (data.iconoCode.includes("02")) iconoClase = "fa-cloud-sun";
                    else if (data.iconoCode.includes("09") || data.iconoCode.includes("10")) iconoClase = "fa-cloud-showers-water";
                    else if (data.iconoCode.includes("11")) iconoClase = "fa-cloud-bolt";
                    else if (data.iconoCode.includes("13")) iconoClase = "fa-snowflake";

                    const descFormateada = data.descripcion.charAt(0).toUpperCase() + data.descripcion.slice(1);
                    elementoTiempo.innerHTML = `<i class="fa-solid ${iconoClase}"></i> PRÍPIAT: ${descFormateada} | TEMP: ${data.temperatura.toFixed(1)}°C`;
                }
            }).catch(error => {
                const elementoTiempo = document.getElementById('monitor-tiempo');
                if (elementoTiempo) elementoTiempo.innerHTML = `<i class="fa-solid fa-triangle-exclamation text-warning"></i> TIEMPO: NO DISPONIBLE`;
            });
    }

    async function verificarEstadoSesion() {
        try {
            const response = await fetch('/usuarios/sesion');
            if (response.ok) {
                const data = await response.json();
                let liContenedor = document.getElementById('zona-usuario-navbar');
                if (!liContenedor) {
                    const enlaceLogin = document.querySelector('a[href="/login.html"]') || document.querySelector('a[href="login.html"]');
                    if (enlaceLogin) liContenedor = enlaceLogin.parentElement;
                }
                if (liContenedor) {
                    let enlacePanel = '/index.html';
                    if (data.rol === 'ROLE_CLIENTE') enlacePanel = '/reservas/mis-reservas.html';
                    else if (data.rol === 'ROLE_ADMIN') enlacePanel = '/intranet/panel-admin.html';
                    else enlacePanel = '/intranet/panel-empleado.html';

                    let iconoUsuario = data.rol === 'ROLE_CLIENTE' ? 'fa-user-astronaut' : 'fa-shield-halved';

                    liContenedor.innerHTML = `
                        <div class="btn-group ms-lg-3 mt-2 mt-lg-0">
                            <a href="${enlacePanel}" class="btn text-white mono fw-bold border-0 d-flex align-items-center bg-transparent">
                                <i class="fa-solid fa-circle text-success shadow-sm" style="font-size: 0.45rem; margin-right: 8px;"></i>
                                <i class="fa-solid ${iconoUsuario} text-white-50 me-2"></i> ${data.usuario}
                            </a>
                            <button type="button" class="btn text-white border-0 dropdown-toggle dropdown-toggle-split bg-transparent" data-bs-toggle="dropdown" aria-expanded="false"></button>
                            <ul class="dropdown-menu dropdown-menu-dark dropdown-menu-end border-secondary mt-2 shadow-lg">
                                <li><a class="dropdown-item text-white-50" href="/logout"><i class="fa-solid fa-right-from-bracket me-2 text-danger"></i>Cerrar sesión</a></li>
                            </ul>
                        </div>`;
                }
            }
        } catch (error) {}
    }

    // =========================================================================
    // BLOQUE 3: ZONA PÚBLICA Y FORMULARIOS
    // =========================================================================
    if (window.location.pathname.includes('login.html')) {
        if (window.location.search.includes('error')) {
            const errorLogin = document.getElementById('errorLogin');
            if (errorLogin) errorLogin.classList.remove('d-none');
        }
    }

    const formRegistro = document.getElementById('formularioRegistro');
    if (formRegistro) {
        formRegistro.addEventListener('submit', async function(e) {
            e.preventDefault();
            const btn = document.getElementById('btnRegistrar');
            const alerta = document.getElementById('mensajeAlerta');
            const checkbox = document.getElementById('aceptoTerminos');
            if (!checkbox.checked) {
                alerta.className = "alert alert-warning small mono fw-bold text-center";
                alerta.innerHTML = '<i class="fa-solid fa-triangle-exclamation me-2"></i> Es obligatorio aceptar los Términos.';
                alerta.classList.remove('d-none');
                return;
            }
            btn.disabled = true;
            btn.innerText = "Procesando...";
            const datos = {
                cuenta: document.getElementById('cuenta').value, clave: document.getElementById('clave').value,
                nombre: document.getElementById('nombre').value, apellido1: document.getElementById('apellido1').value,
                apellido2: document.getElementById('apellido2').value, dni: document.getElementById('dni').value,
                fechaNacimiento: document.getElementById('fechaNacimiento').value, nacionalidad: document.getElementById('nacionalidad').value,
                email: document.getElementById('email').value, telefono: document.getElementById('telefono').value,
                consentimiento: checkbox.checked
            };
            try {
                const response = await fetch('/usuarios/registro-cliente', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(datos) });
                const mensaje = await response.text();
                if (response.ok) {
                    alerta.className = "alert alert-success small mono fw-bold text-center";
                    alerta.innerHTML = '<i class="fa-solid fa-check-circle me-2"></i> ' + mensaje;
                    alerta.classList.remove('d-none');
                    setTimeout(() => { window.location.href = "/index.html"; }, 2000);
                } else {
                    alerta.className = "alert alert-danger small mono fw-bold text-center";
                    alerta.innerHTML = '<i class="fa-solid fa-skull-crossbones me-2"></i> ' + mensaje;
                    alerta.classList.remove('d-none');
                    btn.disabled = false; btn.innerText = "Procesar Alta";
                }
            } catch (error) {
                btn.disabled = false; btn.innerText = "Procesar Alta";
            }
        });
    }

    const formVIP = document.getElementById('formularioVIP');
    if (formVIP) {
        fetch('/clientes/mi-perfil').then(res => res.ok ? res.json() : window.location.href = "/login.html").then(cliente => {
            if (cliente) {
                document.getElementById('titularNombre').value = cliente.nombre;
                document.getElementById('titularApellidos').value = cliente.apellido1 + (cliente.apellido2 ? ' ' + cliente.apellido2 : '');
                document.getElementById('titularDni').value = cliente.dni;
                document.getElementById('titularNacionalidad').value = cliente.nacionalidad;
                document.getElementById('titularNacimiento').value = cliente.fechaNacimiento;
                document.getElementById('titularEmail').value = cliente.email;
                document.getElementById('badgeAutofill').classList.remove('d-none');
            }
        });

        let numAcompanantes = 0;
        document.getElementById('btnAddClient')?.addEventListener('click', () => {
            if (numAcompanantes < 9) {
                numAcompanantes++;
                const idUnico = 'acomp_' + numAcompanantes;
                const formHtml = `
                    <div class="companion-card p-3 p-md-4 rounded mb-4 position-relative" id="tarjeta_${idUnico}">
                        <button type="button" class="btn btn-danger btn-sm position-absolute top-0 end-0 m-3" onclick="document.getElementById('tarjeta_${idUnico}').remove();"><i class="fa-solid fa-xmark"></i></button>
                        <h6 class="mono text-white mb-3">Acompañante ${numAcompanantes}</h6>
                        <div class="row g-3 inputs-acompanante">
                            <div class="col-md-4"><label class="form-label text-white-50 small mono">Nombre Real</label><input type="text" class="form-control form-control-dark form-control-sm nombre-acomp" required></div>
                            <div class="col-md-4"><label class="form-label text-white-50 small mono">Apellidos</label><input type="text" class="form-control form-control-dark form-control-sm apellidos-acomp" required></div>
                            <div class="col-md-4"><label class="form-label text-white-50 small mono">Pasaporte</label><input type="text" class="form-control form-control-dark form-control-sm dni-acomp" required></div>
                        </div>
                    </div>`;
                document.getElementById('contenedorAcompanantes').insertAdjacentHTML('beforeend', formHtml);
            }
        });

        formVIP.addEventListener('submit', async function(e) {
            e.preventDefault();
            const btn = document.getElementById('btnVIP');
            btn.disabled = true; btn.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin me-2"></i> Procesando...';

            const payload = {
                fechaInicio: document.getElementById('fechaInicio').value,
                fechaAlternativa: document.getElementById('fechaAlt').value,
                observaciones: document.querySelector('textarea').value,
                titularNombre: document.getElementById('titularNombre').value + ' ' + document.getElementById('titularApellidos').value,
                titularEmail: document.getElementById('titularEmail').value,
                acompanantes: Array.from(document.querySelectorAll('.inputs-acompanante')).map(t => ({ nombre: t.querySelector('.nombre-acomp').value, apellidos: t.querySelector('.apellidos-acomp').value, dni: t.querySelector('.dni-acomp').value }))
            };
            try {
                const response = await fetch('/api/contacto/vip', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
                if (response.ok) {
                    btn.classList.add('d-none');
                    document.getElementById('alertaExito').classList.remove('d-none');
                    setTimeout(() => { window.location.href = "/index.html"; }, 4500);
                }
            } catch (error) { alert("Error de conexión"); }
        });
    }

    if (window.location.pathname.includes('paquetes.html')) {
        fetch('/paquetes')
            .then(res => res.json())
            .then(paquetes => {
                const contenedor = document.getElementById('contenedorPaquetes');
                if (!contenedor) return;
                contenedor.innerHTML = '';
                paquetes.forEach(p => {
                    let colorPeligro = p.nivelpeligro.nivel === 'BAJO' ? 'success' : (p.nivelpeligro.nivel === 'MEDIO' ? 'warning' : 'danger');
                    let icono = p.nivelpeligro.nivel === 'BAJO' ? 'fa-book-open' : (p.nivelpeligro.nivel === 'MEDIO' ? 'fa-microscope' : 'fa-radiation');
                    let botonReserva = p.nombre.includes("VIP")
                        ? `<a href="/contacto.html" class="btn btn-outline-danger w-100 mono fw-bold text-uppercase mb-2"><i class="fa-solid fa-crown me-2"></i> Solicitar VIP</a>`
                        : `<a href="/reservas/paso1-datos.html?idPaquete=${p.id}" class="btn btn-outline-${colorPeligro} w-100 mono fw-bold text-uppercase mb-2"><i class="fa-solid fa-file-signature me-2"></i> Iniciar Reserva</a>`;

                    let html = `
                    <div class="col-md-6 col-lg-4">
                        <div class="card h-100 border-0 shadow-sm border-top border-${colorPeligro} border-3">
                            <div class="card-body d-flex flex-column p-4">
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <span class="badge bg-${colorPeligro} bg-opacity-10 text-${colorPeligro} border border-${colorPeligro} mono"><i class="fa-solid ${icono} me-1"></i> RIESGO ${p.nivelpeligro.nivel}</span>
                                    <span class="fw-bold fs-5 text-dark">${p.precioPaquete} €</span>
                                </div>
                                <h5 class="card-title fw-bold">${p.nombre}</h5>
                                <p class="small text-muted mb-3 flex-grow-1 text-justify">${p.descripcion}</p>
                                ${botonReserva}
                                <a href="/itinerarios.html#paquete-${p.id}" class="btn btn-light border w-100 mono fw-bold text-uppercase text-muted"><i class="fa-solid fa-route me-2"></i> Ver Itinerario</a>
                            </div>
                        </div>
                    </div>`;
                    contenedor.insertAdjacentHTML('beforeend', html);
                });
            });
    }

    // =========================================================================
    // BLOQUE 4: ÁREA DEL CLIENTE Y RESERVAS
    // =========================================================================
    if (window.location.pathname.includes('paso1-datos.html')) {
        const urlParams = new URLSearchParams(window.location.search);
        let idPaquete = urlParams.get('idPaquete');

        if (!idPaquete && sessionStorage.getItem('reservaMemoria')) {
            idPaquete = JSON.parse(sessionStorage.getItem('reservaMemoria')).idPaquete;
        }

        if (idPaquete) {
            fetch('/paquetes/' + idPaquete).then(res => res.json()).then(p => {
                document.getElementById('nombrePaqueteUI').innerText = p.nombre + " (Tarifa Plana: " + p.precioPaquete + "€)";
                sessionStorage.setItem('paqueteMemoria', JSON.stringify({ id: p.id, nombre: p.nombre, precio: p.precioPaquete }));
            });
        }

        fetch('/clientes/mi-perfil').then(res => res.ok ? res.json() : window.location.href = "/login.html").then(c => {
            document.getElementById('titularNombre').value = c.nombre + " " + c.apellido1;
            document.getElementById('titularDni').value = c.dni;
            document.getElementById('titularEmail').value = c.email;
        });

        let numPax = 1;
        document.getElementById('btnAddAcomp')?.addEventListener('click', () => {
            if (numPax < 10) {
                const idU = Date.now();
                const html = `
                <div class="row g-2 mb-3 p-3 rounded bg-white border companion-row">
                    <div class="col-md-3"><input type="text" class="form-control form-control-sm name-inp" placeholder="Nombre" required></div>
                    <div class="col-md-3"><input type="text" class="form-control form-control-sm ape-inp" placeholder="Apellidos" required></div>
                    <div class="col-md-3"><input type="text" class="form-control form-control-sm dni-inp" placeholder="DNI/Pasaporte" required></div>
                    <div class="col-md-3"><input type="text" class="form-control form-control-sm nac-inp" placeholder="Nacionalidad" required></div>
                    <button type="button" class="btn btn-sm btn-danger mt-2" onclick="this.parentElement.remove();"><i class="fa-solid fa-trash"></i> Eliminar Pasajero</button>
                </div>`;
                document.getElementById('listaAcompanantes').insertAdjacentHTML('beforeend', html);
                numPax++;
            }
        });

        document.getElementById('formPaso1')?.addEventListener('submit', (e) => {
            e.preventDefault();
            const paqueteInfo = JSON.parse(sessionStorage.getItem('paqueteMemoria'));
            const reservaData = {
                idPaquete: paqueteInfo.id, nombrePaquete: paqueteInfo.nombre, precioTotal: paqueteInfo.precio,
                fechaViaje: document.getElementById('fechaViaje').value, telefonoContacto: document.getElementById('telefonoContacto').value,
                titular: document.getElementById('titularNombre').value,
                acompanantes: Array.from(document.querySelectorAll('.companion-row')).map(row => ({
                    nombre: row.querySelector('.name-inp').value, apellidos: row.querySelector('.ape-inp').value,
                    dni: row.querySelector('.dni-inp').value, nacionalidad: row.querySelector('.nac-inp').value
                }))
            };
            sessionStorage.setItem('reservaMemoria', JSON.stringify(reservaData));
            window.location.href = 'paso2-resumen.html';
        });
    }

    if (window.location.pathname.includes('paso2-resumen.html')) {
        const data = JSON.parse(sessionStorage.getItem('reservaMemoria'));
        if (!data) window.location.href = '/paquetes.html';

        document.getElementById('resumenPaquete').innerText = data.nombrePaquete;
        document.getElementById('resumenFecha').innerText = data.fechaViaje;
        document.getElementById('resumenPax').innerText = (data.acompanantes.length + 1) + " Persona(s)";
        document.getElementById('resumenPrecio').innerText = data.precioTotal + " €";

        let listaHTML = `<li>${data.titular} (Titular de Misión)</li>`;
        data.acompanantes.forEach(a => { listaHTML += `<li>${a.nombre} ${a.apellidos} (${a.dni})</li>`; });
        document.getElementById('resumenNombres').innerHTML = listaHTML;

        document.getElementById('btnVolverPaso1').addEventListener('click', () => { window.location.href = 'paso1-datos.html'; });
        document.getElementById('btnConfirmarResumen').addEventListener('click', () => { window.location.href = 'paso3-pago.html'; });
    }

    if (window.location.pathname.includes('paso3-pago.html')) {
        const data = JSON.parse(sessionStorage.getItem('reservaMemoria'));
        if (!data) window.location.href = '/paquetes.html';

        document.getElementById('tarjetaNum')?.addEventListener('input', e => document.getElementById('cardShow').innerText = e.target.value || "**** **** **** ****");
        document.getElementById('tarjetaTitular')?.addEventListener('input', e => document.getElementById('nameShow').innerText = e.target.value || "TITULAR");

        document.getElementById('formPaso3')?.addEventListener('submit', async function(e) {
            e.preventDefault();
            const btn = document.getElementById('btnPagarFinal');
            const alerta = document.getElementById('alertaPago');
            btn.disabled = true;
            btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin me-2"></i> Validando...';

            const payload = {
                idPaquete: data.idPaquete, fechaViaje: data.fechaViaje, telefonoContacto: data.telefonoContacto,
                acompanantes: data.acompanantes
            };

            try {
                const res = await fetch('/api/reservas/tramitar', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
                if (res.ok) {
                    sessionStorage.removeItem('reservaMemoria');
                    sessionStorage.removeItem('paqueteMemoria');
                    btn.classList.add('d-none');
                    alerta.classList.remove('d-none', 'alert-danger');
                    alerta.classList.add('alert-success');
                    alerta.innerHTML = '<i class="fa-solid fa-check-circle me-2"></i> Pago autorizado y expedición registrada. Redirigiendo...';
                    setTimeout(() => { window.location.href = "/reservas/mis-reservas.html"; }, 3000);
                } else throw new Error();
            } catch (error) {
                btn.disabled = false;
                btn.innerHTML = '<i class="fa-solid fa-lock me-2"></i> Procesar y Enviar';
                alerta.classList.remove('d-none');
                alerta.classList.add('alert-danger');
                alerta.innerText = "Error en la pasarela bancaria. Inténtelo de nuevo.";
            }
        });
    }

    if (window.location.pathname.includes('mis-reservas.html')) {
        const cargarTablaReservas = () => {
            fetch('/api/reservas/mis-expediciones')
                .then(res => res.ok ? res.json() : window.location.href = "/login.html")
                .then(reservas => {
                    const cont = document.getElementById('contenedorMisReservas');
                    if (!cont) return;

                    if (reservas.length === 0) {
                        cont.innerHTML = `
                            <div class="text-center py-5">
                                <i class="fa-solid fa-folder-open fa-4x text-muted mb-3 opacity-50"></i>
                                <h4 class="fw-bold text-dark">No hay expediciones en curso</h4>
                                <a href="/paquetes.html" class="btn btn-danger mono fw-bold mt-3 px-4 py-2 shadow-sm">Explorar Catálogo</a>
                            </div>`;
                        return;
                    }

                    let filasHTML = '';
                    const hoy = new Date();

                    reservas.forEach(r => {
                        let badgeClass = "bg-secondary"; let iconClass = "fa-circle-info";
                        let esActiva = false;

                        if (r.estado === "PENDIENTE") { badgeClass = "bg-warning text-dark"; iconClass = "fa-clock"; esActiva = true; }
                        else if (r.estado === "PAGADA" || r.estado === "ACEPTADA") { badgeClass = "bg-success"; iconClass = "fa-check"; esActiva = true; }
                        else if (r.estado === "CANCELADA") { badgeClass = "bg-danger"; iconClass = "fa-xmark"; }

                        const fechaViaje = new Date(r.fechaViaje);
                        const diasRestantes = Math.ceil((fechaViaje - hoy) / (1000 * 60 * 60 * 24));

                        let btnDetalle = `<button class="btn btn-sm btn-outline-info mono" onclick="verDetalleExpedicion(${r.id})"><i class="fa-solid fa-eye"></i> Detalles</button>`;
                        let accionesHTML = '';

                        if (esActiva && diasRestantes > 7) {
                            accionesHTML = `
                                <div class="d-flex flex-column gap-1">
                                    ${btnDetalle}
                                    <button class="btn btn-sm btn-outline-primary mono" onclick="abrirModalModificar(${r.id}, '${r.fechaViaje}')"><i class="fa-solid fa-pen"></i> Fecha</button>
                                    <button class="btn btn-sm btn-outline-danger mono" onclick="cancelarExpedicion(${r.id})"><i class="fa-solid fa-ban"></i> Anular</button>
                                </div>`;
                        } else if (esActiva && diasRestantes <= 7) {
                            accionesHTML = `
                                <div class="d-flex flex-column gap-1">
                                    ${btnDetalle}
                                    <span class="badge bg-secondary mono" title="Faltan ${diasRestantes} días"><i class="fa-solid fa-lock me-1"></i> Bloqueada</span>
                                </div>`;
                        } else {
                            accionesHTML = `
                                <div class="d-flex flex-column gap-1 align-items-center">
                                    ${btnDetalle}
                                    <span class="text-muted small italic text-center mt-1">Archivada</span>
                                </div>`;
                        }

                        filasHTML += `
                            <tr>
                                <td class="mono fw-bold text-dark">${r.localizadorCliente}</td>
                                <td class="fw-bold text-muted">${r.tipoPaquete ? r.tipoPaquete.nombre : 'Descatalogado'}</td>
                                <td class="mono">${r.fechaViaje}</td>
                                <td><span class="badge ${badgeClass} mono"><i class="fa-solid ${iconClass} me-1"></i> ${r.estado}</span></td>
                                <td class="text-center align-middle">${accionesHTML}</td>
                            </tr>`;
                    });

                    cont.innerHTML = `
                        <table class="table table-hover align-middle border">
                            <thead class="table-dark mono small"><tr><th>LOCALIZADOR</th><th>EXPEDICIÓN</th><th>FECHA ENTRADA</th><th>ESTADO</th><th class="text-center">GESTIÓN</th></tr></thead>
                            <tbody>${filasHTML}</tbody>
                        </table>`;
                });
        };
        cargarTablaReservas();

        window.verDetalleExpedicion = async function(id) {
            try {
                const res = await fetch('/api/reservas/' + id);
                if (!res.ok) throw new Error();
                const r = await res.json();

                document.getElementById('detalleLocalizador').innerText = r.localizadorCliente;
                document.getElementById('detalleEstado').innerText = r.estado;
                document.getElementById('detallePaquete').innerText = r.tipoPaquete ? r.tipoPaquete.nombre : "No disponible";
                document.getElementById('detalleFecha').innerText = r.fechaViaje;
                document.getElementById('detallePrecio').innerText = r.precioTotal + " €";

                let viajerosHTML = '';
                r.viajeros.forEach((v, index) => {
                    let esTitular = index === 0 ? '<span class="badge bg-dark ms-2">TITULAR</span>' : '';
                    viajerosHTML += `<li class="list-group-item bg-light"><i class="fa-solid fa-user text-muted me-2"></i> ${v.nombre} ${v.apellido1} (DNI: ${v.dni}) ${esTitular}</li>`;
                });
                document.getElementById('detalleViajeros').innerHTML = viajerosHTML;

                new bootstrap.Modal(document.getElementById('modalDetalleReserva')).show();
            } catch (error) { alert("Error de conexión al cargar los detalles."); }
        };

        window.cancelarExpedicion = async function(id) {
            if (confirm("⚠️ ¿Está seguro de que desea cancelar esta expedición? Esta acción es irreversible.")) {
                try {
                    const res = await fetch('/api/reservas/' + id + '/cancelar', { method: 'PUT' });
                    if (res.ok) { alert("Expedición cancelada correctamente."); cargarTablaReservas(); }
                    else { const err = await res.text(); alert("No se pudo cancelar: " + err); }
                } catch (e) { alert("Error de conexión al intentar cancelar."); }
            }
        };

        window.abrirModalModificar = function(id, fechaActual) {
            document.getElementById('modificarIdReserva').value = id;
            document.getElementById('modificarFechaViaje').value = fechaActual;
            new bootstrap.Modal(document.getElementById('modalModificarReserva')).show();
        };

        document.getElementById('formModificarReserva')?.addEventListener('submit', async function(e) {
            e.preventDefault();
            const id = document.getElementById('modificarIdReserva').value;
            const nuevaFecha = document.getElementById('modificarFechaViaje').value;
            try {
                const resGet = await fetch('/api/reservas/' + id);
                if (!resGet.ok) throw new Error("No se pudo leer la reserva");
                const reservaActual = await resGet.json();
                reservaActual.fechaViaje = nuevaFecha;

                const resPut = await fetch('/api/reservas/actualizar/' + id, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(reservaActual) });
                if (resPut.ok) {
                    alert("Fecha modificada correctamente.");
                    bootstrap.Modal.getInstance(document.getElementById('modalModificarReserva')).hide();
                    cargarTablaReservas();
                } else { alert("No se pudo modificar. Aforo excedido."); }
            } catch (e) { alert("Error de conexión."); }
        });
    }

    if (window.location.pathname.includes('perfil.html')) {
        let estadoActivo = true;

        const listaAvatares = [
            "babushka_01.jpg", "babushka_resiliente_01.png", "cientifico_01.jpg", "cientifico_02.jpg",
            "comandante_01.jpg", "comandante_operaciones_01.png", "conductor_01.jpg", "conductor_transporte_01.png",
            "especialista_traje_pesado_01.png", "fisica_01.jpg", "fisica_nuclear_analitica_01.png", "hazmat_01.jpg",
            "cientifica_campo_01.png", "ingeniero_01.jpg", "ingeniero_hardware_01.png", "liquidador_biorobot_01.png",
            "oficial_liquidacion_01.png", "paramedica_01.jpg", "paramedica_campo_01.png", "tactico_01.jpg"
        ];

        fetch('/usuarios/sesion')
            .then(res => res.ok ? res.json() : window.location.href = "/login.html")
            .then(sesion => {
                if (sesion.rol !== 'ROLE_CLIENTE') {
                    alert("Acceso denegado. Ficha exclusiva de exploradores.");
                    window.location.href = "/intranet/panel-admin.html";
                } else {
                    cargarDatosPerfil();
                    generarGaleriaAvatares();
                }
            }).catch(() => window.location.href = "/login.html");

        function cargarDatosPerfil() {
            fetch('/clientes/mi-perfil')
                .then(res => res.ok ? res.json() : window.location.href = "/login.html")
                .then(c => {
                    document.getElementById('uiCuenta').innerText = c.usuario ? c.usuario.cuenta : 'Sin Cuenta';
                    document.getElementById('uiId').innerText = Math.floor(Math.random() * 90000) + 10000;
                    document.getElementById('perfilId').value = c.id; document.getElementById('perfilDni').value = c.dni;
                    document.getElementById('perfilNombre').value = c.nombre; document.getElementById('perfilApe1').value = c.apellido1;
                    document.getElementById('perfilApe2').value = c.apellido2 || ''; document.getElementById('perfilFechaNac').value = c.fechaNacimiento;
                    document.getElementById('perfilNacionalidad').value = c.nacionalidad; document.getElementById('perfilEmail').value = c.email;
                    document.getElementById('perfilTelefono').value = c.telefono; estadoActivo = c.activo;

                    // Reset visual de la contraseña por seguridad
                    document.getElementById('perfilClave').value = '********';
                    document.getElementById('perfilClaveConfirmar').value = '';

                    const fotoGuardada = localStorage.getItem('avatar_' + c.id);
                    if (fotoGuardada) {
                        document.getElementById('avatarPreview').src = fotoGuardada;
                        document.getElementById('avatarPreview').classList.remove('d-none');
                        document.getElementById('defaultAvatarIcon').classList.add('d-none');
                    }

                    fetch('/api/reservas/dosimetria/' + c.dni).then(r => r.text()).then(mSv => {
                        let valorRadiacion = parseFloat(mSv); if (isNaN(valorRadiacion)) valorRadiacion = 0.00;
                        document.getElementById('uiRadiacion').innerText = valorRadiacion.toFixed(2);
                    }).catch(() => document.getElementById('uiRadiacion').innerText = "0.00");
                });
        }

        function generarGaleriaAvatares() {
            const contenedor = document.getElementById('rejillaAvatares');
            if (!contenedor) return;
            let html = '';
            listaAvatares.forEach(nombreFoto => {
                const rutaAbsoluta = `/img/avatar/${nombreFoto}`;
                html += `
	                    <div class="col-4 col-sm-3 col-md-2">
	                        <img src="${rutaAbsoluta}" class="img-thumbnail bg-dark border-secondary w-100 shadow-sm" style="cursor: pointer; object-fit: cover; aspect-ratio: 1/1; transition: 0.2s;" onmouseover="this.style.borderColor='#ffc107'" onmouseout="this.style.borderColor='#6c757d'" onclick="seleccionarAvatarPredefinido('${rutaAbsoluta}')" title="${nombreFoto}">
	                    </div>`;
            });
            contenedor.innerHTML = html;
        }

        window.seleccionarAvatarPredefinido = function(rutaAbsoluta) {
            const idCliente = document.getElementById('perfilId').value;
            localStorage.setItem('avatar_' + idCliente, rutaAbsoluta);
            document.getElementById('avatarPreview').src = rutaAbsoluta;
            document.getElementById('avatarPreview').classList.remove('d-none'); document.getElementById('defaultAvatarIcon').classList.add('d-none');
            bootstrap.Modal.getInstance(document.getElementById('modalSelectorAvatar')).hide();
        };

        document.getElementById('inputAvatarManual')?.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(evento) {
                    const base64Img = evento.target.result;
                    const idCliente = document.getElementById('perfilId').value;
                    localStorage.setItem('avatar_' + idCliente, base64Img);
                    document.getElementById('avatarPreview').src = base64Img;
                    document.getElementById('avatarPreview').classList.remove('d-none'); document.getElementById('defaultAvatarIcon').classList.add('d-none');
                    bootstrap.Modal.getInstance(document.getElementById('modalSelectorAvatar')).hide();
                };
                reader.readAsDataURL(file);
            }
        });

        // HABILITAR EDICIÓN: Limpia la contraseña visual y muestra el campo de confirmación
        document.getElementById('btnHabilitarEdicion')?.addEventListener('click', () => {
            document.querySelectorAll('.input-editable').forEach(inp => { inp.removeAttribute('readonly'); inp.classList.add('edit-mode'); });
            document.getElementById('btnHabilitarEdicion').classList.add('d-none'); document.getElementById('grupoBotonesGuardar').classList.remove('d-none');

            const inputClave = document.getElementById('perfilClave');
            inputClave.value = '';
            inputClave.placeholder = 'Dejar en blanco para no cambiar';
            document.getElementById('divClaveConfirmar').classList.remove('d-none');
        });

        // CANCELAR EDICIÓN: Restaura la contraseña visual y oculta la confirmación
        document.getElementById('btnCancelarEdicion')?.addEventListener('click', () => {
            document.querySelectorAll('.input-editable').forEach(inp => { inp.setAttribute('readonly', true); inp.classList.remove('edit-mode'); });
            document.getElementById('btnHabilitarEdicion').classList.remove('d-none'); document.getElementById('grupoBotonesGuardar').classList.add('d-none');

            document.getElementById('divClaveConfirmar').classList.add('d-none');
            document.getElementById('errorClave').classList.add('d-none');
            cargarDatosPerfil();
        });

        // GUARDAR PERFIL CON DOBLE VALIDACIÓN DE CONTRASEÑA
        document.getElementById('formPerfilCliente')?.addEventListener('submit', async function(e) {
            e.preventDefault();
            const btn = document.getElementById('btnGuardarPerfil');
            const alerta = document.getElementById('alertaPerfil');
            const id = document.getElementById('perfilId').value;
            const clave1 = document.getElementById('perfilClave').value;
            const clave2 = document.getElementById('perfilClaveConfirmar').value;
            const errorClave = document.getElementById('errorClave');

            // Validación estricta de contraseñas si el usuario intentó escribir algo
            if (clave1 !== '' || clave2 !== '') {
                if (clave1 !== clave2) {
                    errorClave.classList.remove('d-none');
                    return; // Abortamos el guardado
                }
            }
            errorClave.classList.add('d-none');

            btn.disabled = true; btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin me-2"></i>Sellando...';

            const payload = {
                dni: document.getElementById('perfilDni').value, nombre: document.getElementById('perfilNombre').value,
                apellido1: document.getElementById('perfilApe1').value, apellido2: document.getElementById('perfilApe2').value,
                fechaNacimiento: document.getElementById('perfilFechaNac').value, nacionalidad: document.getElementById('perfilNacionalidad').value,
                email: document.getElementById('perfilEmail').value, telefono: document.getElementById('perfilTelefono').value, activo: estadoActivo
            };

            // Solo enviamos la clave si realmente la quiere cambiar
            if (clave1 !== '') {
                payload.clave = clave1;
            }

            try {
                const res = await fetch('/clientes/actualizar/' + id, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
                if (res.ok) {
                    alerta.classList.remove('d-none', 'alert-danger'); alerta.classList.add('alert-success');
                    alerta.innerHTML = '<i class="fa-solid fa-check me-2"></i>Expediente actualizado.';
                    document.getElementById('btnCancelarEdicion').click();
                } else throw new Error(await res.text());
            } catch (error) {
                alerta.classList.remove('d-none', 'alert-success'); alerta.classList.add('alert-danger');
                alerta.innerHTML = '<i class="fa-solid fa-xmark me-2"></i>' + error.message;
            } finally { btn.disabled = false; btn.innerHTML = '<i class="fa-solid fa-floppy-disk me-2"></i>Sellar Cambios'; }
        });

        document.getElementById('btnDarDeBaja')?.addEventListener('click', async () => {
            if (confirm("⚠️ ADVERTENCIA DEL SBU: Está a punto de revocar sus credenciales permanentemente. ¿Desea proceder?")) {
                const id = document.getElementById('perfilId').value;
                try {
                    const res = await fetch('/clientes/' + id, { method: 'DELETE' });
                    if (res.ok) { alert("Credenciales revocadas."); window.location.href = "/logout"; }
                    else { alert("Error al procesar baja."); }
                } catch (error) { alert("Error de conexión."); }
            }
        });
    }
    // =========================================================================
    // BLOQUE 5: INTRANET SBU Y GESTOR DE RESERVAS
    // =========================================================================
    const esPaginaIntranet = window.location.pathname.includes('panel-admin.html') ||
        window.location.pathname.includes('panel-empleado.html') ||
        window.location.pathname.includes('gestion-reservas.html');

    if (esPaginaIntranet) {

        // 5.1 CARGAR CREDENCIALES ACTIVAS (Funciona en Panel Admin y en Gestión Reservas)
        fetch('/usuarios/sesion')
            .then(res => res.ok ? res.json() : window.location.href = "/login.html")
            .then(sesion => {
                if (sesion.rol === 'ROLE_CLIENTE') { window.location.href = "/perfil.html"; return; }

                const uiCuenta = document.getElementById('empCuenta');
                const uiRol = document.getElementById('empRol');
                if (uiCuenta) uiCuenta.innerText = sesion.usuario;

                let rolFormateado = sesion.rol.replace('ROLE_', '');
                if (rolFormateado === 'ADMIN') rolFormateado = 'ADMINISTRADOR DE SISTEMAS';
                else if (rolFormateado === 'SUPERVISOR') rolFormateado = 'SUPERVISOR DE ZONA';
                else if (rolFormateado === 'EMPLEADO') rolFormateado = 'OPERARIO DE DATOS';
                if (uiRol) uiRol.innerText = rolFormateado;

                fetch('/clientes/mi-perfil').then(res => res.ok ? res.json() : { error: true }).then(perfil => {
                    if (perfil.error) throw new Error();
                    const elNombre = document.getElementById('empNombre'); const elApe = document.getElementById('empApellidos');
                    if (elNombre) elNombre.innerText = perfil.nombre;
                    if (elApe) elApe.innerText = perfil.apellido1 + " " + (perfil.apellido2 || "");
                    let ultimosDni = perfil.dni ? perfil.dni.slice(-4).toUpperCase() : "XXXX";
                    const badgeCodigo = document.getElementById('empCodigoInterno');
                    if (badgeCodigo) badgeCodigo.innerHTML = `<i class="fa-solid fa-barcode me-1"></i> CÓD. SBU-${ultimosDni}`;
                }).catch(() => {
                    const elNombre = document.getElementById('empNombre'); const badgeCodigo = document.getElementById('empCodigoInterno');
                    if (sesion.rol === 'ROLE_ADMIN') {
                        if (elNombre) elNombre.innerText = "SISTEMA";
                        if (badgeCodigo) badgeCodigo.innerHTML = '<i class="fa-solid fa-barcode me-1"></i> CÓD. ROOT-0000';
                    }
                });
            }).catch(() => window.location.href = "/login.html");

        // 5.2 LÓGICA EXCLUSIVA DEL PANEL DE ADMINISTRACIÓN
        if (window.location.pathname.includes('panel-admin.html')) {
            fetch('/clientes/empleados-activos')
                .then(res => res.json())
                .then(empleados => {
                    const ul = document.getElementById('listaEmpleadosActivos');
                    if (!ul) return;
                    ul.innerHTML = '';

                    // FILTRO: Solo los que están online
                    const online = empleados.filter(e => e.online === true);

                    if (online.length === 0) {
                        ul.innerHTML = '<li class="list-group-item bg-transparent text-center text-muted-sbu small py-2">Sin personal activo</li>';
                    } else {
                        online.forEach(e => {
                            ul.innerHTML += `
		                            <li class="list-group-item bg-transparent text-white border-corporate py-2 d-flex justify-content-between align-items-center">
		                                <div>
		                                    <i class="fa-solid fa-circle text-success shadow-sm me-2" style="font-size: 0.5rem;"></i>
		                                    ${e.nombre}
		                                    <div class="small text-muted-sbu ms-3">${e.rol}</div>
		                                </div>
		                            </li>`;
                        });
                    }
                }).catch(() => {
                    const ul = document.getElementById('listaEmpleadosActivos');
                    if (ul) ul.innerHTML = '<li class="list-group-item bg-transparent text-center text-danger small">Error al conectar con la red</li>';
                });

            // Tabla Top 10 (Llama directo a tu endpoint Backend de Urgentes)
            fetch('/api/reservas/pendientes-urgentes')
                .then(res => res.json())
                .then(reservas => {
                    const tbody = document.getElementById('tablaReservasPendientes');
                    if (!tbody) return;
                    tbody.innerHTML = '';
                    if (reservas.length === 0) {
                        tbody.innerHTML = '<tr><td colspan="4" class="text-center text-success py-4 bg-transparent border-0"><i class="fa-solid fa-check-double fa-2x mb-2"></i><br>BANDEJA LIMPIA</td></tr>'; return;
                    }

                    const hoy = new Date();
                    reservas.forEach(r => {
                        const diasDif = Math.ceil((new Date(r.fechaViaje) - hoy) / (1000 * 60 * 60 * 24));
                        const esUrgente = diasDif <= 60;
                        const badgeHTML = esUrgente
                            ? `<span class="badge bg-danger"><i class="fa-solid fa-triangle-exclamation"></i> URGENTE (${diasDif}d)</span>`
                            : `<span class="badge bg-warning text-dark"><i class="fa-solid fa-clock"></i> PENDIENTE</span>`;
                        const claseFila = esUrgente ? 'bg-danger bg-opacity-10' : 'bg-transparent';

                        tbody.innerHTML += `
	                            <tr class="${claseFila}">
	                                <td class="text-white border-corporate py-3">${r.localizadorCliente || 'S/N'}</td>
	                                <td class="text-white-50 border-corporate">${r.fechaViaje}</td>
	                                <td class="text-center border-corporate">${badgeHTML}</td>
	                                <td class="text-end border-corporate">
	                                    <a href="/intranet/gestion-reservas.html" class="btn btn-sm btn-outline-secondary mono">Gestionar</a>
	                                </td>
	                            </tr>`;
                    });
                }).catch(() => {});
        }

        // 5.3 LÓGICA EXCLUSIVA DEL GESTOR DE RESERVAS (Todas, con filtros)
        if (window.location.pathname.includes('gestion-reservas.html')) {
            let todasLasReservas = [];
            let ordenDesc = false;

            const tbodySBU = document.getElementById('tablaGestorReservas');
            const filtroEst = document.getElementById('filtroEstadoReserva');
            const busq = document.getElementById('buscadorLocalizador');
            const btnOrd = document.getElementById('btnInvertirOrden');

            function renderizarCompleto() {
                if (!tbodySBU) return;
                tbodySBU.innerHTML = '';

                let filtradas = todasLasReservas.filter(r => {
                    const est = filtroEst.value;
                    const txt = busq.value.trim().toUpperCase();
                    let mEst = (est === 'TODAS') ? true : (est === 'CONFIRMADA' ? ['ACEPTADA', 'PAGADA', 'CONFIRMADA'].includes(r.estado) : r.estado === est);
                    let mTxt = txt === '' || (r.localizadorCliente && r.localizadorCliente.toUpperCase().includes(txt));
                    return mEst && mTxt;
                });

                filtradas.sort((a, b) => {
                    if (!a.fechaViaje) return 1; if (!b.fechaViaje) return -1;
                    return ordenDesc ? new Date(b.fechaViaje) - new Date(a.fechaViaje) : new Date(a.fechaViaje) - new Date(b.fechaViaje);
                });

                if (filtradas.length === 0) { tbodySBU.innerHTML = '<tr><td colspan="5" class="text-center text-muted-sbu py-4 border-0">No hay resultados.</td></tr>'; return; }

                filtradas.forEach(r => {
                    let badgeClass = 'bg-secondary';
                    let claseFila = 'bg-transparent';
                    if (r.estado === 'PENDIENTE') badgeClass = 'bg-warning text-dark';
                    else if (['ACEPTADA', 'PAGADA', 'CONFIRMADA'].includes(r.estado)) badgeClass = 'bg-success';
                    else if (r.estado === 'CANCELADA') { badgeClass = 'bg-danger'; claseFila = 'opacity-50'; }

                    tbodySBU.innerHTML += `
	                        <tr class="${claseFila}">
	                            <td class="text-white border-corporate py-3 fw-bold">${r.localizadorCliente}</td>
	                            <td class="text-white-50 border-corporate">${r.fechaViaje}</td>
	                            <td class="text-white-50 border-corporate">${r.tipoPaquete ? r.tipoPaquete.nombre : 'No asignado'}</td>
	                            <td class="text-center border-corporate"><span class="badge ${badgeClass}">${r.estado}</span></td>
	                            <td class="text-end border-corporate">
	                                <button class="btn btn-sm btn-outline-info mono" onclick="abrirModalSBU(${r.id})">Inspeccionar</button>
	                            </td>
	                        </tr>`;
                });
            }

            fetch('/api/reservas').then(res => res.json()).then(data => { todasLasReservas = data; renderizarCompleto(); });

            if (filtroEst) filtroEst.addEventListener('change', renderizarCompleto);
            if (busq) busq.addEventListener('input', renderizarCompleto);
            if (btnOrd) btnOrd.addEventListener('click', () => {
                ordenDesc = !ordenDesc; document.getElementById('iconoOrden').className = ordenDesc ? "fa-solid fa-arrow-up-wide-short me-1" : "fa-solid fa-arrow-down-short-wide me-1";
                renderizarCompleto();
            });

            // Lógica Modal (Modificar y Aprobar/Rechazar)
            window.abrirModalSBU = function(id) {
                const r = todasLasReservas.find(x => x.id === id);
                if (!r) return;

                document.getElementById('modIdReserva').value = r.id;
                document.getElementById('modLoc').innerText = r.localizadorCliente;
                document.getElementById('modPaquete').innerText = r.tipoPaquete ? r.tipoPaquete.nombre : 'S/N';
                document.getElementById('modFechaViaje').value = r.fechaViaje;
                document.getElementById('modTelefono').value = r.telefono;
                document.getElementById('modEmail').value = r.emailContacto;
                document.getElementById('modObservaciones').value = r.observaciones || '';

                const infoGestor = r.empleadoGestor ? ` (Modificado por: ${r.empleadoGestor})` : '';
                document.getElementById('modEstado').innerText = r.estado + infoGestor;

                let vHTML = '';
                r.viajeros.forEach(v => vHTML += `<li class="list-group-item bg-dark text-white-50 border-secondary"><i class="fa-solid fa-user me-2"></i>${v.nombre} ${v.apellido1} (DNI: ${v.dni})</li>`);
                document.getElementById('modViajeros').innerHTML = vHTML;

                const btnAprobar = document.getElementById('btnAprobarSBU');
                const btnRechazar = document.getElementById('btnRechazarSBU');
                if (r.estado === 'PENDIENTE') {
                    btnAprobar.classList.remove('d-none'); btnRechazar.classList.remove('d-none');
                } else {
                    btnAprobar.classList.add('d-none'); btnRechazar.classList.add('d-none');
                }

                new bootstrap.Modal(document.getElementById('modalExpedienteSBU')).show();
            };

            document.getElementById('formModificarReservaSBU')?.addEventListener('submit', async function(e) {
                e.preventDefault();
                const id = document.getElementById('modIdReserva').value;
                const payload = {
                    fechaViaje: document.getElementById('modFechaViaje').value,
                    telefono: document.getElementById('modTelefono').value,
                    emailContacto: document.getElementById('modEmail').value,
                    observaciones: document.getElementById('modObservaciones').value
                };
                try {
                    const res = await fetch(`/api/reservas/actualizar-sbu/${id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
                    if (res.ok) { alert("Expediente modificado con éxito."); location.reload(); } else alert("Error de servidor.");
                } catch (e) { alert("Error de red."); }
            });

            document.getElementById('btnAprobarSBU')?.addEventListener('click', async function() {
                if (confirm("¿Autorizar expedición?")) {
                    const id = document.getElementById('modIdReserva').value;
                    await fetch(`/api/reservas/${id}/aprobar`, { method: 'PUT' });
                    location.reload();
                }
            });

            document.getElementById('btnRechazarSBU')?.addEventListener('click', async function() {
                const motivo = prompt("Indique el motivo del rechazo (obligatorio):");
                if (motivo && motivo.trim() !== "") {
                    const id = document.getElementById('modIdReserva').value;
                    await fetch(`/api/reservas/${id}/rechazar?motivo=${encodeURIComponent(motivo)}`, { method: 'PUT' });
                    location.reload();
                }
            });
        }
    }

    // =========================================================================
    // BLOQUE 5C: LÓGICA DE EMPLEADOS (Página empleados.html)
    // =========================================================================
    if (window.location.pathname.includes('empleados.html')) {
        let listaCompleta = [];
        let miRolActual = 'EMPLEADO';

        // --- 1. LÓGICA DE SEGURIDAD VISUAL Y SESIÓN ---
        fetch('/usuarios/sesion')
            .then(res => res.json())
            .then(data => {
                miRolActual = data.rol.replace('ROLE_', '');
                if (miRolActual === 'ADMIN' || miRolActual === 'SUPERVISOR') {
                    const btnNuevo = document.getElementById('btnNuevoEmpleado');
                    if (btnNuevo) btnNuevo.classList.remove('d-none');
                }
            })
            .catch(err => console.error("Error al verificar permisos:", err));

        // --- 2. RENDERIZADO DE LA LISTA DE EMPLEADOS ---
        const renderizarEmpleados = (filtro = "") => {
            const ul = document.getElementById('listaEmpleadosSoloOnline');
            if (!ul) return;
            ul.innerHTML = '';

            const filtrados = listaCompleta.filter(e =>
                e.nombre.toLowerCase().includes(filtro.toLowerCase()) ||
                e.rol.toLowerCase().includes(filtro.toLowerCase())
            ).sort((a, b) => b.online - a.online);

            if (filtrados.length === 0) {
                ul.innerHTML = '<li class="list-group-item bg-transparent text-center text-muted-sbu py-5 border-0">SIN RESULTADOS</li>';
                return;
            }

            filtrados.forEach(e => {
                const colorEstado = e.online ? 'text-success' : 'text-secondary';
                const estadoHTML = e.online
                    ? '<span class="text-success fw-bold">ONLINE</span>'
                    : `<span class="text-muted-sbu">Última: ${e.ultimaConexion}</span>`;
                const iconoEstado = e.online ? 'fa-circle' : 'fa-circle-dot';

                ul.innerHTML += `
		                    <li class="list-group-item bg-transparent text-white border-corporate py-3 d-flex align-items-center justify-content-between" 
	                            style="cursor: pointer; transition: 0.2s;" onmouseover="this.classList.add('bg-dark')" onmouseout="this.classList.remove('bg-dark')"
	                            onclick="abrirModalEmpleado(${e.id})">
		                        <div class="d-flex align-items-center">
		                            <i class="fa-solid ${iconoEstado} ${colorEstado} me-3" style="font-size: 0.6rem;"></i>
		                            <div>
		                                <div class="fw-bold">${e.nombre}</div>
		                                <div class="small text-muted-sbu">${e.rol}</div>
		                            </div>
		                        </div>
		                        
		                        <div class="d-flex align-items-center gap-3 text-end" style="min-width: 140px;">
		                            <div class="d-flex gap-2">
		                                <a href="mailto:${e.email}?subject=Consulta%20desde%20Intranet%20SBU" 
		                                   class="btn btn-sm btn-outline-secondary ${!e.email ? 'disabled' : ''} text-corporate" 
		                                   title="Enviar Email" onclick="event.stopPropagation()">
		                                    <i class="fa-solid fa-envelope"></i>
		                                </a>
		                                <a href="tel:${e.telefono}" 
		                                   class="btn btn-sm btn-outline-secondary ${!e.telefono ? 'disabled' : ''} text-corporate" 
		                                   title="Llamar" onclick="event.stopPropagation()">
		                                    <i class="fa-solid fa-phone"></i>
		                                </a>
		                            </div>
		                            <small class="mono d-none d-sm-block" style="width: 80px;">${estadoHTML}</small>
		                        </div>
		                    </li>`;
            });
        };

        const cargarEmpleadosApi = () => {
            fetch('/clientes/empleados-activos')
                .then(res => res.json())
                .then(data => {
                    listaCompleta = data;
                    renderizarEmpleados();
                })
                .catch(err => console.error("Error al cargar empleados:", err));
        }
        cargarEmpleadosApi();

        document.getElementById('buscadorEmpleados')?.addEventListener('input', (e) => {
            renderizarEmpleados(e.target.value);
        });

        // --- 3. LÓGICA DEL MODAL DE EDICIÓN / ELIMINACIÓN ---
        window.abrirModalEmpleado = async function(id) {
            try {
                const res = await fetch('/clientes/' + id);
                if (!res.ok) throw new Error("Ficha no encontrada");
                const emp = await res.json();

                document.getElementById('editIdEmp').value = emp.id;
                document.getElementById('editDniEmp').value = emp.dni;
                document.getElementById('editNombreEmp').value = emp.nombre;
                document.getElementById('editApe1Emp').value = emp.apellido1;
                document.getElementById('editApe2Emp').value = emp.apellido2 || '';
                document.getElementById('editNacimientoEmp').value = emp.fechaNacimiento;
                document.getElementById('editNacionalidadEmp').value = emp.nacionalidad;
                document.getElementById('editTelefonoEmp').value = emp.telefono;
                document.getElementById('editEmailEmp').value = emp.email;

                const fotoGuardada = localStorage.getItem('avatar_' + emp.id);
                const imgPreview = document.getElementById('empAvatarPreview');
                const iconDefault = document.getElementById('empDefaultAvatarIcon');

                if (fotoGuardada) {
                    imgPreview.src = fotoGuardada; imgPreview.classList.remove('d-none'); iconDefault.classList.add('d-none');
                } else {
                    imgPreview.src = ''; imgPreview.classList.add('d-none'); iconDefault.classList.remove('d-none');
                }

                // Control de Seguridad Estricto: Solo ADMIN puede modificar o eliminar
                const btnEliminar = document.getElementById('btnEliminarEmp');
                const btnGuardar = document.getElementById('btnGuardarEdicionEmp');
                const labelFoto = document.querySelector('label[for="inputEmpAvatar"]');
                const inputsFicha = document.querySelectorAll('#formEdicionEmpleado input:not(#editDniEmp):not([type="hidden"])');

                if (miRolActual === 'ADMIN') {
                    // ADMIN: Ve botones y puede editar inputs
                    btnEliminar.classList.remove('d-none');
                    btnGuardar.classList.remove('d-none');
                    if (labelFoto) labelFoto.classList.remove('d-none');
                    inputsFicha.forEach(inp => inp.removeAttribute('readonly'));
                } else {
                    // OTROS: No ven botones y inputs son readonly (solo lectura)
                    btnEliminar.classList.add('d-none');
                    btnGuardar.classList.add('d-none');
                    if (labelFoto) labelFoto.classList.add('d-none');
                    inputsFicha.forEach(inp => inp.setAttribute('readonly', 'true'));
                }

                document.getElementById('alertaEdicionEmp').classList.add('d-none');
                new bootstrap.Modal(document.getElementById('modalEdicionEmpleado')).show();

            } catch (error) {
                alert("Error de red al cargar la ficha del empleado.");
            }
        };

        // Cambio manual de avatar
        document.getElementById('inputEmpAvatar')?.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(evento) {
                    const base64Img = evento.target.result;
                    const id = document.getElementById('editIdEmp').value;
                    localStorage.setItem('avatar_' + id, base64Img);
                    const imgPreview = document.getElementById('empAvatarPreview');
                    imgPreview.src = base64Img; imgPreview.classList.remove('d-none');
                    document.getElementById('empDefaultAvatarIcon').classList.add('d-none');
                };
                reader.readAsDataURL(file);
            }
        });

        // Evento: Guardar Modificación
        document.getElementById('formEdicionEmpleado')?.addEventListener('submit', async function(e) {
            e.preventDefault();
            if (miRolActual !== 'ADMIN') return; // Cortafuegos doble por si alteran el HTML manual

            const btn = document.getElementById('btnGuardarEdicionEmp');
            const alerta = document.getElementById('alertaEdicionEmp');
            const id = document.getElementById('editIdEmp').value;
            btn.disabled = true;

            const payload = {
                dni: document.getElementById('editDniEmp').value,
                nombre: document.getElementById('editNombreEmp').value,
                apellido1: document.getElementById('editApe1Emp').value,
                apellido2: document.getElementById('editApe2Emp').value,
                fechaNacimiento: document.getElementById('editNacimientoEmp').value,
                nacionalidad: document.getElementById('editNacionalidadEmp').value,
                email: document.getElementById('editEmailEmp').value,
                telefono: document.getElementById('editTelefonoEmp').value,
                activo: true
            };

            try {
                const res = await fetch('/clientes/actualizar/' + id, {
                    method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload)
                });
                if (res.ok) {
                    alerta.className = "alert alert-success mono small fw-bold mt-2";
                    alerta.innerHTML = '<i class="fa-solid fa-check me-2"></i>Ficha de empleado actualizada.';
                    cargarEmpleadosApi();
                } else throw new Error("Error en la validación de los datos.");
            } catch (error) {
                alerta.className = "alert alert-danger mono small fw-bold mt-2";
                alerta.innerHTML = '<i class="fa-solid fa-xmark me-2"></i>' + error.message;
            } finally {
                btn.disabled = false;
            }
        });

        // Evento: Eliminar Personal
        document.getElementById('btnEliminarEmp')?.addEventListener('click', async () => {
            if (miRolActual !== 'ADMIN') return; // Cortafuegos doble

            if (confirm("⚠️ ALERTA CRÍTICA: Estás a punto de ELIMINAR FÍSICAMENTE a un miembro del personal de la base de datos. ¿Confirmas esta acción?")) {
                const id = document.getElementById('editIdEmp').value;
                try {
                    const res = await fetch(`/clientes/${id}`, { method: 'DELETE' });
                    if (res.ok) {
                        alert("Miembro del personal purgado del sistema.");
                        localStorage.removeItem('avatar_' + id);
                        bootstrap.Modal.getInstance(document.getElementById('modalEdicionEmpleado')).hide();
                        cargarEmpleadosApi();
                    } else { alert("Error: El sistema rechazó la purga."); }
                } catch (error) { alert("Error crítico de conexión."); }
            }
        });
    }

    // =========================================================================
    // BLOQUE 5D: ALTA EMPLEADO (Página alta-empleado.html)
    // =========================================================================
    if (window.location.pathname.includes('alta-empleado.html')) {

        // 1. Verificar sesión y cargar roles permitidos
        fetch('/usuarios/sesion')
            .then(res => res.json())
            .then(data => {
                const contenedor = document.getElementById('contenedorRol');

                // Si un empleado raso intenta entrar, lo bloqueamos y expulsamos
                if (data.rol === 'ROLE_EMPLEADO') {
                    contenedor.innerHTML = '<span class="text-danger small mono fw-bold">ACCESO DENEGADO</span>';
                    document.getElementById('btnProcesarAlta').disabled = true;
                    alert("Violación de seguridad: No tienes permisos para dar de alta personal.");
                    window.location.href = '/intranet/empleados.html';
                    return;
                }

                // Generar el select dinámicamente según el rol
                let opciones = '';
                if (data.rol === 'ROLE_ADMIN') {
                    opciones = `
		                    <option value="ROLE_SUPERVISOR">SUPERVISOR</option>
		                    <option value="ROLE_EMPLEADO">EMPLEADO</option>
		                `;
                } else if (data.rol === 'ROLE_SUPERVISOR') {
                    opciones = `<option value="ROLE_EMPLEADO">EMPLEADO</option>`;
                }

                // Inyectamos el select en el HTML
                contenedor.innerHTML = `
		                <label class="form-label small text-muted-sbu mono">Asignación de Rol</label>
		                <select class="form-select bg-dark text-white border-corporate" id="altaRol" required>
		                    ${opciones}
		                </select>
		            `;
            })
            .catch(err => console.error("Error al verificar sesión:", err));

        // 2. Procesar el envío del formulario
        const formAlta = document.getElementById('formularioAltaEmpleado');
        if (formAlta) {
            formAlta.addEventListener('submit', function(e) {
                e.preventDefault();

                // Mapeamos los inputs a los nombres exactos que espera el Backend
                const payload = {
                    cuenta: document.getElementById('altaCuenta').value,
                    clave: document.getElementById('altaClave').value,
                    rol: document.getElementById('altaRol').value,
                    nombre: document.getElementById('altaNombre').value,
                    apellido1: document.getElementById('altaApe1').value,
                    apellido2: document.getElementById('altaApe2').value,
                    dni: document.getElementById('altaDni').value,
                    nacionalidad: document.getElementById('altaNacionalidad').value,
                    fechaNacimiento: document.getElementById('altaNacimiento').value,
                    email: document.getElementById('altaEmail').value,
                    telefono: document.getElementById('altaTelefono').value
                };

                fetch('/usuarios/registro-empleado', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                })
                    .then(async res => {
                        const alerta = document.getElementById('alertaRegistro');
                        alerta.classList.remove('d-none');
                        const textoServidor = await res.text();

                        if (res.ok) {
                            alerta.className = "alert alert-success mono small fw-bold text-center mb-4";
                            alerta.innerHTML = `<i class="fa-solid fa-check-circle me-2"></i>${textoServidor}`;
                            this.reset(); // Limpiamos formulario tras éxito
                        } else {
                            alerta.className = "alert alert-danger mono small fw-bold text-center mb-4";
                            alerta.innerHTML = `<i class="fa-solid fa-triangle-exclamation me-2"></i>${textoServidor}`;
                        }
                    })
                    .catch(err => console.error("Error en petición de alta:", err));
            });
        }
    }

    // =========================================================================
    // BLOQUE 5E: HERRAMIENTAS DE INTELIGENCIA (herramientas-sbu.html)
    // =========================================================================
    if (window.location.pathname.includes('herramientas-sbu.html')) {

        let datosActualesAudit = [];
        let operadorSBU = { rol: "ADMIN", cuenta: "Sistema" };
        const tbodyResultados = document.getElementById('tablaResultadosAudit');
        const msjError = document.getElementById('mensajeErrorAudit');
        const btnExportar = document.getElementById('btnExportarCsv');
        const selectOrden = document.getElementById('selectOrdenacionAudit');

        // 1. Obtener la identidad del empleado de forma segura
        fetch('/usuarios/sesion')
            .then(res => res.json())
            .then(data => {
                if (data.rol) operadorSBU.rol = data.rol.replace('ROLE_', '');
                // Buscamos la cuenta en las propiedades habituales devueltas por Spring Security
                operadorSBU.cuenta = data.cuenta || data.username || data.nombre || 'Sistema';
            }).catch(() => console.warn("Modo offline: Empleado no detectado."));

        // 2. Función para pintar la tabla
        const dibujarFilas = (datos) => {
            tbodyResultados.innerHTML = '';

            if (!datos || datos.length === 0) {
                tbodyResultados.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-warning">NO SE ENCONTRARON REGISTROS</td></tr>';
                btnExportar.classList.add('d-none');
                selectOrden.classList.add('d-none');
                return;
            }

            datos.forEach(c => {
                // Recuperar la última acción sobre este cliente desde la memoria local
                const registroOperacion = JSON.parse(localStorage.getItem('operacion_sbu_' + c.id));
                let infoOperacionHtml = '';

                if (registroOperacion) {
                    let colorAccion = 'text-warning';
                    let iconAccion = 'fa-pen-to-square';
                    let textoAccion = 'CLIENTE MODIFICADO';

                    if (registroOperacion.accion === 'BAJA') {
                        colorAccion = 'text-danger';
                        iconAccion = 'fa-skull-crossbones';
                        textoAccion = 'DADO DE BAJA';
                    }
                    if (registroOperacion.accion === 'ALTA') {
                        colorAccion = 'text-success';
                        iconAccion = 'fa-heart-pulse';
                        textoAccion = 'CLIENTE REACTIVADO';
                    }

                    // Se inyecta el cuadro con el texto exacto solicitado debajo del estado
                    infoOperacionHtml = `
	                        <div class="small mt-2 p-1 border border-secondary rounded bg-dark text-center lh-sm" style="font-size: 0.70rem;">
	                            <span class="${colorAccion} fw-bold"><i class="fa-solid ${iconAccion} me-1"></i>${textoAccion} POR:</span><br>
	                            <span class="text-white-50">${registroOperacion.rol} - ${registroOperacion.cuenta}</span>
	                        </div>
	                    `;
                }

                // Generación visual del Estado
                let estadoIcon = '';
                if (c.activo === false) {
                    estadoIcon = '<span class="badge bg-danger mb-1">INACTIVO / BAJA</span>';
                } else {
                    estadoIcon = c.consentimiento
                        ? '<i class="fa-solid fa-check text-success fa-lg" title="Activo y con Consentimiento"></i>'
                        : '<i class="fa-solid fa-xmark text-warning fa-lg" title="Falta Consentimiento"></i>';
                }

                const tr = document.createElement('tr');
                tr.style.cursor = 'pointer';
                tr.onclick = () => abrirModalInspeccion(c.id);
                if (c.activo === false) tr.classList.add('opacity-50');

                tr.innerHTML = `
	                    <td class="align-middle">${c.dni || 'N/A'}</td>
	                    <td class="align-middle fw-bold text-corporate">${c.nombre} ${c.apellido1 || ''} ${c.apellido2 || ''}</td>
	                    <td class="align-middle">${c.nacionalidad || 'N/A'}</td>
	                    <td class="align-middle">${c.fechaNacimiento || 'N/A'}</td>
	                    <td class="align-middle">${c.telefono || c.email || 'Sin contacto'}</td>
	                    <td class="align-middle d-flex flex-column align-items-center justify-content-center">
	                        ${estadoIcon}
	                        ${infoOperacionHtml}
	                    </td>
	                `;
                tbodyResultados.appendChild(tr);
            });

            btnExportar.classList.remove('d-none');
            if (datos.length > 1) selectOrden.classList.remove('d-none');
            else selectOrden.classList.add('d-none');
        };

        const aplicarOrdenacion = () => {
            const criterio = selectOrden.value;
            let datosClonados = [...datosActualesAudit];

            if (criterio === 'defecto') {
                datosClonados.sort((a, b) => (a.activo === b.activo) ? 0 : a.activo ? -1 : 1);
            } else {
                switch (criterio) {
                    case 'alfabetico': datosClonados.sort((a, b) => (a.nombre || '').localeCompare(b.nombre || '')); break;
                    case 'edad_desc': datosClonados.sort((a, b) => new Date(b.fechaNacimiento) - new Date(a.fechaNacimiento)); break;
                    case 'edad_asc': datosClonados.sort((a, b) => new Date(a.fechaNacimiento) - new Date(b.fechaNacimiento)); break;
                    case 'consentimiento': datosClonados.sort((a, b) => (a.consentimiento === b.consentimiento) ? 0 : a.consentimiento ? 1 : -1); break;
                }
            }
            dibujarFilas(datosClonados);
        };

        const procesarDatosApi = (datos) => {
            msjError.classList.add('d-none');
            if (!datos) datos = [];

            // 1. Convertimos la respuesta en un Array manejable
            let arrayDatos = Array.isArray(datos) ? datos : [datos];

            // 2. FILTRO ESTRICTO: Descartamos cualquier ficha que pertenezca a un empleado
            datosActualesAudit = arrayDatos.filter(c => c.usuario && c.usuario.rol === 'ROLE_CLIENTE');

            // 3. Si después de filtrar a los empleados nos quedamos a cero, mostramos aviso
            if (datosActualesAudit.length === 0) {
                mostrarErrorAudit("No se encontraron clientes con esos criterios.");
                return;
            }

            selectOrden.value = 'defecto';
            aplicarOrdenacion();
        };

        const mostrarErrorAudit = (texto) => {
            btnExportar.classList.add('d-none');
            selectOrden.classList.add('d-none');
            tbodyResultados.innerHTML = '';
            msjError.innerText = texto;
            msjError.classList.remove('d-none');
        };

        selectOrden?.addEventListener('change', aplicarOrdenacion);

        // --- MÉTODOS DE BÚSQUEDA ---
        document.getElementById('btnBuscarDni')?.addEventListener('click', () => {
            const dni = document.getElementById('inputDniAudit').value.trim();
            if (!dni) return;
            fetch(`/clientes/buscar-dni/${dni}`).then(async res => {
                if (!res.ok) throw new Error("Expediente no encontrado.");
                return res.json();
            }).then(procesarDatosApi).catch(err => mostrarErrorAudit(err.message));
        });

        document.getElementById('btnBuscarTelefono')?.addEventListener('click', () => {
            const tlf = document.getElementById('inputTelefonoAudit').value.trim();
            if (!tlf) return;
            fetch(`/clientes/buscar-telefono/${encodeURIComponent(tlf)}`).then(async res => {
                if (!res.ok) throw new Error("No existe ningún cliente con ese teléfono.");
                return res.json();
            }).then(procesarDatosApi).catch(err => mostrarErrorAudit(err.message));
        });

        document.getElementById('btnBuscarNacionalidad')?.addEventListener('click', () => {
            const pais = document.getElementById('inputNacionalidadAudit').value.trim();
            if (!pais) return;
            fetch(`/clientes/buscar-nacionalidad-mayores?nacionalidad=${encodeURIComponent(pais)}`).then(async res => {
                if (!res.ok) throw new Error("No hay expedientes con esta nacionalidad.");
                const data = await res.json();
                if (data.length === 0) throw new Error("No hay expedientes con esta nacionalidad.");
                return data;
            }).then(procesarDatosApi).catch(err => mostrarErrorAudit(err.message));
        });

        document.getElementById('btnBuscarMenores')?.addEventListener('click', () => {
            fetch(`/clientes/buscar-menores`).then(async res => {
                if (!res.ok) throw new Error("No hay menores en el sistema.");
                const data = await res.json();
                if (data.length === 0) throw new Error("No hay menores en el sistema.");
                return data;
            }).then(procesarDatosApi).catch(err => mostrarErrorAudit(err.message));
        });

        document.getElementById('btnMostrarTodos')?.addEventListener('click', () => {
            fetch(`/clientes`).then(async res => {
                if (!res.ok) throw new Error("Error recuperando la base de datos.");
                const data = await res.json();
                if (data.length === 0) throw new Error("La base de datos está vacía.");
                return data;
            }).then(procesarDatosApi).catch(err => mostrarErrorAudit(err.message));
        });

        document.getElementById('btnBuscarInactivos')?.addEventListener('click', () => {
            fetch(`/clientes`).then(async res => {
                if (!res.ok) throw new Error("Error recuperando la base de datos.");
                const data = await res.json();
                const inactivos = data.filter(c => c.activo === false);
                if (inactivos.length === 0) throw new Error("No hay ningún cliente inactivo o dado de baja.");
                return inactivos;
            }).then(procesarDatosApi).catch(err => mostrarErrorAudit(err.message));
        });

        // --- LÓGICA DEL MODAL DE EDICIÓN ---
        window.abrirModalInspeccion = function(id) {
            const c = datosActualesAudit.find(x => x.id === id);
            if (!c) return;

            document.getElementById('editIdAudit').value = c.id;
            document.getElementById('editActivoAudit').value = c.activo;
            document.getElementById('editDniAudit').value = c.dni;
            document.getElementById('editNombreAudit').value = c.nombre;
            document.getElementById('editApe1Audit').value = c.apellido1;
            document.getElementById('editApe2Audit').value = c.apellido2 || '';
            document.getElementById('editNacimientoAudit').value = c.fechaNacimiento;
            document.getElementById('editNacionalidadAudit').value = c.nacionalidad;
            document.getElementById('editTelefonoAudit').value = c.telefono;
            document.getElementById('editEmailAudit').value = c.email;

            // Gestión visual de Botones Baja / Alta según el estado del cliente
            const btnBaja = document.getElementById('btnDarBajaAudit');
            const btnAlta = document.getElementById('btnDarAltaAudit');

            if (c.activo === false) {
                btnBaja.classList.add('d-none');
                btnAlta.classList.remove('d-none');
            } else {
                btnBaja.classList.remove('d-none');
                btnAlta.classList.add('d-none');
            }

            const fotoGuardada = localStorage.getItem('avatar_' + c.id);
            const imgPreview = document.getElementById('auditAvatarPreview');
            const iconDefault = document.getElementById('auditDefaultAvatarIcon');

            if (fotoGuardada) {
                imgPreview.src = fotoGuardada;
                imgPreview.classList.remove('d-none');
                iconDefault.classList.add('d-none');
            } else {
                imgPreview.src = '';
                imgPreview.classList.add('d-none');
                iconDefault.classList.remove('d-none');
            }

            document.getElementById('alertaEdicionAudit').classList.add('d-none');
            new bootstrap.Modal(document.getElementById('modalEdicionClienteAudit')).show();
        };

        // Guardar cambios del cliente (Forzar Actualización)
        document.getElementById('formEdicionClienteAudit')?.addEventListener('submit', async function(e) {
            e.preventDefault();
            const btn = document.getElementById('btnGuardarEdicionAudit');
            const alerta = document.getElementById('alertaEdicionAudit');
            const id = document.getElementById('editIdAudit').value;
            btn.disabled = true;

            const payload = {
                dni: document.getElementById('editDniAudit').value,
                nombre: document.getElementById('editNombreAudit').value,
                apellido1: document.getElementById('editApe1Audit').value,
                apellido2: document.getElementById('editApe2Audit').value,
                fechaNacimiento: document.getElementById('editNacimientoAudit').value,
                nacionalidad: document.getElementById('editNacionalidadAudit').value,
                email: document.getElementById('editEmailAudit').value,
                telefono: document.getElementById('editTelefonoAudit').value,
                activo: document.getElementById('editActivoAudit').value === 'true'
            };

            try {
                const res = await fetch('/clientes/actualizar/' + id, {
                    method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload)
                });
                if (res.ok) {
                    alerta.className = "alert alert-success mono small fw-bold mt-2";
                    alerta.innerHTML = '<i class="fa-solid fa-check me-2"></i>Datos forzados y guardados.';
                    const clienteActualizado = await res.json();

                    localStorage.setItem('operacion_sbu_' + id, JSON.stringify({ accion: 'MODIFICADO', rol: operadorSBU.rol, cuenta: operadorSBU.cuenta }));

                    const indice = datosActualesAudit.findIndex(x => x.id == id);
                    if (indice !== -1) datosActualesAudit[indice] = clienteActualizado;
                    aplicarOrdenacion();
                } else throw new Error("Fallo de integridad de datos.");
            } catch (error) {
                alerta.className = "alert alert-danger mono small fw-bold mt-2";
                alerta.innerHTML = '<i class="fa-solid fa-xmark me-2"></i>' + error.message;
            } finally {
                btn.disabled = false;
            }
        });

        // Acción: Dar de Baja Lógica
        document.getElementById('btnDarBajaAudit')?.addEventListener('click', async () => {
            if (confirm("⚠️ ¿Confirmas que deseas dar de baja este expediente?")) {
                const id = document.getElementById('editIdAudit').value;
                try {
                    const res = await fetch(`/clientes/${id}/baja`, { method: 'PUT' });
                    if (res.ok) {
                        alert("Expediente dado de baja lógicamente.");
                        localStorage.setItem('operacion_sbu_' + id, JSON.stringify({ accion: 'BAJA', rol: operadorSBU.rol, cuenta: operadorSBU.cuenta }));
                        bootstrap.Modal.getInstance(document.getElementById('modalEdicionClienteAudit')).hide();

                        const indice = datosActualesAudit.findIndex(x => x.id == id);
                        if (indice !== -1) datosActualesAudit[indice].activo = false;
                        aplicarOrdenacion();
                    } else { alert("Error al procesar la baja en servidor."); }
                } catch (error) { alert("Error crítico de red."); }
            }
        });

        // Acción: Reactivar Cliente (Dar de Alta)
        document.getElementById('btnDarAltaAudit')?.addEventListener('click', async () => {
            if (confirm("✅ ¿Confirmas que deseas reactivar a este cliente y devolverle el acceso al sistema?")) {
                const id = document.getElementById('editIdAudit').value;
                try {
                    const res = await fetch(`/clientes/${id}/alta`, { method: 'PUT' });
                    if (res.ok) {
                        alert("Expediente reactivado con éxito.");
                        localStorage.setItem('operacion_sbu_' + id, JSON.stringify({ accion: 'ALTA', rol: operadorSBU.rol, cuenta: operadorSBU.cuenta }));
                        bootstrap.Modal.getInstance(document.getElementById('modalEdicionClienteAudit')).hide();

                        const indice = datosActualesAudit.findIndex(x => x.id == id);
                        if (indice !== -1) datosActualesAudit[indice].activo = true;
                        aplicarOrdenacion();
                    } else { alert("Error al procesar el alta en servidor."); }
                } catch (error) { alert("Error crítico de red."); }
            }
        });

        // Cambio manual de avatar en memoria
        document.getElementById('inputAuditAvatar')?.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(evento) {
                    const base64Img = evento.target.result;
                    const idCliente = document.getElementById('editIdAudit').value;
                    localStorage.setItem('avatar_' + idCliente, base64Img);

                    const imgPreview = document.getElementById('auditAvatarPreview');
                    imgPreview.src = base64Img;
                    imgPreview.classList.remove('d-none');
                    document.getElementById('auditDefaultAvatarIcon').classList.add('d-none');
                };
                reader.readAsDataURL(file);
            }
        });

        document.getElementById('btnExportarCsv')?.addEventListener('click', () => {
            if (datosActualesAudit.length === 0) return;
            let csvContent = "DNI,NOMBRE,APELLIDOS,NACIONALIDAD,FECHA_NACIMIENTO,TELEFONO,EMAIL,ESTADO\n";
            datosActualesAudit.forEach(c => {
                const apellidos = `${c.apellido1 || ''} ${c.apellido2 || ''}`.trim();
                const estadoTxt = c.activo === false ? "BAJA" : "ACTIVO";
                const fila = `"${c.dni || ''}","${c.nombre || ''}","${apellidos}","${c.nacionalidad || ''}","${c.fechaNacimiento || ''}","${c.telefono || ''}","${c.email || ''}","${estadoTxt}"`;
                csvContent += fila + "\n";
            });
            const blob = new Blob(["\uFEFF" + csvContent], { type: 'text/csv;charset=utf-8;' });
            const link = document.createElement("a");
            const url = URL.createObjectURL(blob);
            link.setAttribute("href", url);
            link.setAttribute("download", `Manifiesto_SBU_${new Date().toISOString().split('T')[0]}.csv`);
            link.style.visibility = 'hidden';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        });
    }

    // =========================================================================
    // BLOQUE 6: DIAGNÓSTICO INTEGRADO (Página diagnostico.html)
    // =========================================================================
    if (window.location.pathname.includes('diagnostico.html')) {

        const actualizarServidor = () => {
            fetch('/api/diagnostico/estado')
                .then(res => {
                    if (!res.ok) throw new Error("Error en servidor");
                    return res.json();
                })
                .then(data => {
                    document.getElementById('peticiones').innerText = data.peticiones;
                    document.getElementById('uptime').innerText = data.uptime;
                    document.getElementById('ramTexto').innerText = `${data.ramUsada}MB / ${data.ramTotal}MB`;
                    document.getElementById('ramBar').style.width = `${data.ramPercent}%`;
                })
                .catch(e => {
                    console.error("Fallo al conectar con el servidor:", e);
                    document.getElementById('uptime').innerText = "ERROR";
                });
        };

        const actualizarLocal = () => {
            document.getElementById('horaLocal').innerText = new Date().toLocaleTimeString();

            // Evitamos repintar el hardware si ya está cargado
            if (document.getElementById('cpuCores').innerText === '--') {
                const canvas = document.createElement('canvas');
                const gl = canvas.getContext('webgl');
                const debugInfo = gl ? gl.getExtension('WEBGL_debug_renderer_info') : null;

                // 1. CPU, RAM y GPU
                const cores = navigator.hardwareConcurrency;
                document.getElementById('cpuCores').innerText = cores ? `${cores} hilos${cores < 10 ? ' (Capado)' : ''}` : "Desconocido";

                const ramInfo = navigator.deviceMemory;
                document.getElementById('ramTotal').innerText = ramInfo ? (ramInfo === 8 ? "≥ 8 GB (Límite)" : `${ramInfo} GB`) : "No disponible";

                document.getElementById('gpuInfo').innerText = debugInfo ? gl.getParameter(debugInfo.UNMASKED_RENDERER_WEBGL).split("Direct3D")[0].substring(0, 35).trim() : "No detectado";

                // 2. NAVEGADOR
                const ua = navigator.userAgent;
                let browser = "Desconocido";
                if (ua.includes("Firefox")) browser = "Mozilla Firefox";
                else if (ua.includes("Opera") || ua.includes("OPR")) browser = "Opera";
                else if (ua.includes("Edge") || ua.includes("Edg")) browser = "Microsoft Edge";
                else if (ua.includes("Chrome")) browser = "Chrome / Brave";
                else if (ua.includes("Safari")) browser = "Apple Safari";
                document.getElementById('browserInfo').innerText = browser;

                // 3. SISTEMA OPERATIVO (Client Hints API para build real)
                const elOsInfo = document.getElementById('osInfo');
                if (elOsInfo) {
                    if (navigator.userAgentData && navigator.userAgentData.getHighEntropyValues) {
                        navigator.userAgentData.getHighEntropyValues(["platformVersion"])
                            .then(uaData => {
                                let osName = navigator.userAgentData.platform;
                                let osVersion = uaData.platformVersion;
                                if (osName === "Windows") {
                                    const majorVersion = parseInt(osVersion.split('.')[0]);
                                    if (majorVersion >= 13) osName = "Windows 11";
                                    else if (majorVersion > 0) osName = "Windows 10";
                                }
                                elOsInfo.innerText = `${osName} (Build ${osVersion})`;
                            })
                            .catch(() => elOsInfo.innerText = "Error leyendo Build");
                    } else {
                        // Fallback si el navegador bloquea la API
                        let osFallback = "Desconocido";
                        if (ua.includes("Win")) osFallback = "Windows";
                        else if (ua.includes("Mac")) osFallback = "macOS";
                        else if (ua.includes("Linux")) osFallback = "GNU/Linux";
                        elOsInfo.innerText = `${osFallback} (Versión oculta)`;
                    }
                }
            }
        };

        actualizarServidor();
        setInterval(actualizarServidor, 3000);

        actualizarLocal();
        setInterval(actualizarLocal, 1000);
    }

}); // Cierre del DOMContentLoaded global