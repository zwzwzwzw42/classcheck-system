/* ========== 公共 JS：token 管理、请求封装、角色跳转、通用工具 ========== */

const TOKEN_KEY = 'classcheck_token';

function getToken() { return localStorage.getItem(TOKEN_KEY); }
function setToken(t) { localStorage.setItem(TOKEN_KEY, t); }
function clearToken() { localStorage.removeItem(TOKEN_KEY); }

/** 角色 → 首页 */
function roleHome(role) {
    return { 1: '/student/home.html', 2: '/teacher/home.html', 3: '/admin/home.html' }[role] || '/login.html';
}

const ROLE_NAME = { 1: '学生', 2: '教师', 3: '管理员' };

/**
 * 统一请求封装，自动携带 token、处理 401
 * @returns 解析后的 JSON body
 */
async function api(url, options = {}) {
    const headers = Object.assign({ 'Content-Type': 'application/json' }, options.headers || {});
    const token = getToken();
    if (token) headers['Authorization'] = 'Bearer ' + token;
    let res;
    try {
        res = await fetch(url, Object.assign({}, options, { headers }));
    } catch (e) {
        throw new Error('网络异常，请确认后端服务已启动');
    }
    const data = await res.json();
    if (data.code === 401) {
        clearToken();
        location.href = '/login.html';
        throw new Error(data.message);
    }
    return data;
}

/** 获取当前登录用户，未登录跳转登录页 */
async function loadCurrentUser() {
    const r = await api('/api/auth/me');
    if (r.code !== 0) {
        location.href = '/login.html';
        return null;
    }
    return r.data;
}

/** 退出登录 */
async function logout() {
    try { await api('/api/auth/logout', { method: 'POST' }); } catch (e) { /* 忽略 */ }
    clearToken();
    location.href = '/login.html';
}

/** 渲染顶部导航栏 */
function renderNavbar(user) {
    const nav = document.getElementById('navbar');
    if (!nav || !user) return;
    nav.innerHTML =
        '<div class="brand">课堂学情监控管理系统</div>' +
        '<div class="user-info">' +
        '<span class="role-badge">' + ROLE_NAME[user.role] + '</span>' +
        '<span>' + user.name + '</span>' +
        '<button onclick="logout()">退出登录</button>' +
        '</div>';
}

/** toast 提示 */
function toast(message, type = 'info') {
    let el = document.getElementById('toast');
    if (!el) {
        el = document.createElement('div');
        el.id = 'toast';
        document.body.appendChild(el);
    }
    el.textContent = message;
    el.className = type;
    el.style.display = 'block';
    clearTimeout(el._timer);
    el._timer = setTimeout(() => { el.style.display = 'none'; }, 2500);
}

/** 页签切换 */
function switchTab(tabName) {
    document.querySelectorAll('.tabs .tab').forEach(t => t.classList.toggle('active', t.dataset.tab === tabName));
    document.querySelectorAll('.tab-panel').forEach(p => p.classList.toggle('active', p.id === 'panel-' + tabName));
}

/** 格式化时间 */
function fmtTime(t) {
    if (!t) return '';
    return String(t).replace('T', ' ').substring(0, 16);
}
