document.addEventListener('DOMContentLoaded', () => {

    document.addEventListener('submit', (e) => {
        const msg = e.target.dataset.confirmar;
        if (msg && !confirm(msg)) {
            e.preventDefault();
        }
    });

    document.querySelectorAll('.alerta.sucesso').forEach((el) => {
        setTimeout(() => { el.style.transition = 'opacity .5s'; el.style.opacity = '0'; }, 4500);
        setTimeout(() => el.remove(), 5100);
    });

    document.addEventListener('click', (e) => {
        document.querySelectorAll('details.submenu[open]').forEach((d) => {
            if (!d.contains(e.target)) d.removeAttribute('open');
        });
    });

    const selMaleta = document.querySelector('[data-trocar-maleta-select]');
    if (selMaleta) {
        selMaleta.addEventListener('change', () => { location.href = selMaleta.value; });
    }

    const inputCor = document.querySelector('input[type=color]');
    const amostra = document.querySelector('[data-amostra-cor]');
    if (inputCor && amostra) {
        const atualizar = () => amostra.style.setProperty('--cor', inputCor.value);
        inputCor.addEventListener('input', atualizar);
        atualizar();
    }

    const previa = document.querySelector('[data-previa]');
    if (previa) {
        const inW = document.querySelector('[data-tam=w]');
        const inH = document.querySelector('[data-tam=h]');
        const selTam = document.querySelector('[data-tamanho-maleta]');
        const selCat = document.querySelector('[data-categoria]');
        const desenhar = () => {
            let w = 1, h = 1;
            if (selTam) {
                const o = selTam.selectedOptions[0];
                w = parseInt(o && o.dataset.w, 10) || 0;
                h = parseInt(o && o.dataset.h, 10) || 0;
            } else if (inW && inH) {
                w = Math.max(1, Math.min(20, parseInt(inW.value, 10) || 1));
                h = Math.max(1, Math.min(15, parseInt(inH.value, 10) || 1));
            }
            previa.style.setProperty('--w', w || 1);
            previa.innerHTML = '<span></span>'.repeat(w * h);
            if (selCat) {
                const o = selCat.selectedOptions[0];
                previa.style.setProperty('--cor', (o && o.dataset.cor) || '');
            }
        };
        [inW, inH, selTam, selCat].forEach((el) => el && el.addEventListener('input', desenhar));
        desenhar();
    }

    const selCategoria = document.querySelector('[data-categoria]');
    if (selCategoria) {
        const mostrar = () => {
            const o = selCategoria.selectedOptions[0];
            const tipo = o ? o.dataset.tipo : '';
            document.querySelectorAll('[data-so-tipo]').forEach((b) => {
                b.hidden = b.dataset.soTipo !== tipo;
            });
        };
        selCategoria.addEventListener('change', mostrar);
        mostrar();
    }

    const previaImg = document.querySelector('[data-imagem-previa]');
    if (previaImg) {
        const caminho = document.querySelector('[data-imagem-caminho]');
        const arquivo = document.querySelector('[data-imagem-arquivo]');
        const inW = document.querySelector('[data-tam=w]');
        const inH = document.querySelector('[data-tam=h]');
        const contexto = (document.querySelector('link[href*="estilo.css"]')?.getAttribute('href') || '/css/estilo.css')
            .replace(/css\/estilo\.css.*$/, '');
        const mostrar = (src) => {
            previaImg.innerHTML = '';
            if (src) {
                const img = document.createElement('img');
                img.src = src;
                img.alt = '';
                previaImg.appendChild(img);
            }
        };
        const tamanho = () => {
            previaImg.style.setProperty('--w', Math.max(1, parseInt(inW.value, 10) || 1));
            previaImg.style.setProperty('--h', Math.max(1, parseInt(inH.value, 10) || 1));
        };
        caminho.addEventListener('input', () => {
            const v = caminho.value.trim();
            mostrar(v ? contexto + v.replace(/^\//, '') : '');
        });
        arquivo.addEventListener('change', () => {
            const f = arquivo.files[0];
            if (f) mostrar(URL.createObjectURL(f));
        });
        [inW, inH].forEach((el) => el && el.addEventListener('input', tamanho));
    }

    const grade = document.getElementById('grade');
    if (grade) {
        iniciarInventario(grade);
    }
});

function iniciarInventario(grade) {
    const base = window.BASE_GUARDADOS || '/guardados';
    const cols = parseInt(grade.dataset.cols, 10);
    const rows = parseInt(grade.dataset.rows, 10);
    const pecas = [...grade.querySelectorAll('.peca')];
    const vazio = document.getElementById('detalhe-vazio');
    const modoCombinar = document.getElementById('modo-combinar');
    let selecionada = null;
    let combinarOrigem = null;
    let arraste = null;

    const dados = (p) => ({
        id: p.dataset.id,
        x: +p.dataset.x, y: +p.dataset.y, w: +p.dataset.w, h: +p.dataset.h,
        rot: p.dataset.rot === 'true'
    });

    function selecionar(peca) {
        pecas.forEach((p) => p.classList.remove('selecionada'));
        document.querySelectorAll('[id^="info-"]').forEach((d) => { d.hidden = true; });
        selecionada = peca;
        if (!peca) {
            vazio.hidden = false;
            return;
        }
        peca.classList.add('selecionada');
        vazio.hidden = true;
        const info = document.getElementById('info-' + peca.dataset.id);
        if (info) info.hidden = false;
        try { sessionStorage.setItem('peca-' + location.pathname, peca.dataset.id); } catch (e) {  }
    }

    try {
        const ultimo = sessionStorage.getItem('peca-' + location.pathname);
        const p = ultimo && grade.querySelector(`.peca[data-id="${ultimo}"]`);
        if (p) selecionar(p);
    } catch (e) {  }

    function iniciarCombinar(id) {
        combinarOrigem = id;
        const origem = grade.querySelector(`.peca[data-id="${id}"]`);
        document.getElementById('combinar-nome').textContent = origem ? origem.dataset.nome : '';
        modoCombinar.hidden = false;
        grade.classList.add('combinando');
    }
    function cancelarCombinar() {
        combinarOrigem = null;
        modoCombinar.hidden = true;
        grade.classList.remove('combinando');
    }
    document.querySelectorAll('[data-combinar]').forEach((b) =>
        b.addEventListener('click', () => iniciarCombinar(b.dataset.id)));

    function enviarCombinar(alvoId) {
        const f = document.getElementById('form-combinar');
        f.action = `${base}/${combinarOrigem}/combinar`;
        f.alvoId.value = alvoId;
        f.submit();
    }

    function celulaDoPonteiro(ev) {
        const r = grade.getBoundingClientRect();
        const cw = r.width / cols;
        const ch = r.height / rows;
        return { cx: Math.floor((ev.clientX - r.left) / cw), cy: Math.floor((ev.clientY - r.top) / ch) };
    }

    function posicaoValida(id, x, y, w, h) {
        if (x < 0 || y < 0 || x + w > cols || y + h > rows) return false;
        return pecas.every((p) => {
            if (p.dataset.id === id) return true;
            const o = dados(p);
            return !(o.x < x + w && x < o.x + o.w && o.y < y + h && y < o.y + o.h);
        });
    }

    function atualizarFantasma(ev) {
        const { cx, cy } = celulaDoPonteiro(ev);
        arraste.x = cx - arraste.pegaX;
        arraste.y = cy - arraste.pegaY;
        const f = arraste.fantasma;
        f.style.gridColumn = `${arraste.x + 1} / span ${arraste.w}`;
        f.style.gridRow = `${arraste.y + 1} / span ${arraste.h}`;
        if (arraste.x < 0 || arraste.y < 0 || arraste.x + arraste.w > cols || arraste.y + arraste.h > rows) {
            f.style.gridColumn = `${Math.max(1, Math.min(cols - arraste.w + 1, arraste.x + 1))} / span ${Math.min(arraste.w, cols)}`;
            f.style.gridRow = `${Math.max(1, Math.min(rows - arraste.h + 1, arraste.y + 1))} / span ${Math.min(arraste.h, rows)}`;
        }
        arraste.valido = posicaoValida(arraste.id, arraste.x, arraste.y, arraste.w, arraste.h);
        f.classList.toggle('invalido', !arraste.valido);
    }

    function girarArraste(ev) {
        if (!arraste || !arraste.ativo) return;
        [arraste.w, arraste.h] = [arraste.h, arraste.w];
        arraste.rot = !arraste.rot;
        arraste.pegaX = Math.min(arraste.pegaX, arraste.w - 1);
        arraste.pegaY = Math.min(arraste.pegaY, arraste.h - 1);
        arraste.fantasma.classList.toggle('rot', arraste.rot);
        arraste.fantasma.style.setProperty('--pw', arraste.w);
        arraste.fantasma.style.setProperty('--ph', arraste.h);
        atualizarFantasma(ev || arraste.ultimoEvento);
    }

    function cancelarArraste() {
        if (!arraste) return;
        if (arraste.fantasma) arraste.fantasma.remove();
        if (arraste.peca) arraste.peca.classList.remove('arrastando');
        grade.classList.remove('arrastando');
        arraste = null;
    }

    pecas.forEach((peca) => {
        peca.addEventListener('pointerdown', (ev) => {
            if (ev.button !== 0) return;
            if (combinarOrigem) {
                ev.preventDefault();
                if (peca.dataset.id !== combinarOrigem) enviarCombinar(peca.dataset.id);
                return;
            }
            const d = dados(peca);
            const { cx, cy } = celulaDoPonteiro(ev);
            arraste = {
                peca, id: d.id, w: d.w, h: d.h, rot: d.rot, x: d.x, y: d.y,
                origemX: d.x, origemY: d.y, origemRot: d.rot,
                pegaX: Math.max(0, Math.min(d.w - 1, cx - d.x)), pegaY: Math.max(0, Math.min(d.h - 1, cy - d.y)),
                inicioX: ev.clientX, inicioY: ev.clientY, ativo: false, valido: true, ultimoEvento: ev
            };
            peca.setPointerCapture(ev.pointerId);
        });

        peca.addEventListener('pointermove', (ev) => {
            if (!arraste || arraste.peca !== peca) return;
            arraste.ultimoEvento = ev;
            if (!arraste.ativo) {
                if (Math.hypot(ev.clientX - arraste.inicioX, ev.clientY - arraste.inicioY) < 6) return;
                arraste.ativo = true;
                selecionar(peca);
                const f = peca.cloneNode(true);
                f.classList.add('fantasma');
                f.classList.remove('selecionada', 'equipada');
                f.removeAttribute('data-id');
                grade.appendChild(f);
                arraste.fantasma = f;
                peca.classList.add('arrastando');
                grade.classList.add('arrastando');
            }
            atualizarFantasma(ev);
        });

        peca.addEventListener('pointerup', () => {
            if (!arraste || arraste.peca !== peca) return;
            if (!arraste.ativo) {
                selecionar(peca);
                arraste = null;
                return;
            }
            const mudou = arraste.x !== arraste.origemX || arraste.y !== arraste.origemY || arraste.rot !== arraste.origemRot;
            if (arraste.valido && mudou) {
                const f = document.getElementById('form-mover');
                f.action = `${base}/${arraste.id}/mover`;
                f.x.value = arraste.x;
                f.y.value = arraste.y;
                f.rotacionado.value = arraste.rot;
                f.submit();
                return;
            }
            cancelarArraste();
        });

        peca.addEventListener('pointercancel', cancelarArraste);
        peca.addEventListener('contextmenu', (ev) => {
            if (arraste && arraste.ativo) {
                ev.preventDefault();
                girarArraste();
            }
        });
    });

    document.addEventListener('keydown', (ev) => {
        if (ev.key === 'Escape') {
            cancelarArraste();
            cancelarCombinar();
        } else if ((ev.key === 'r' || ev.key === 'R') && arraste && arraste.ativo) {
            girarArraste();
        }
    });
}
