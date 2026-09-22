/* ========== 在线讨论区（学生端/教师端共用） ========== */

const DISCUSS_ROLE_NAME = { 1: '学生', 2: '教师', 3: '管理员' };
let discussUser = null;
let discussTimer = null;
let discussPendingImage = null; // 待发送的已上传图片URL
let discussPinned = true;      // 是否贴底（用户上滑查看历史时不强制滚动）

function esc(s) {
    return String(s == null ? '' : s).replace(/[&<>"']/g, c => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    }[c]));
}

async function initDiscussion(user) {
    discussUser = user;
    const root = document.getElementById('discuss-root');
    if (!root) return;

    root.innerHTML =
        '<div class="discuss-box">' +
            '<div class="discuss-list" id="discuss-list"></div>' +
            '<div class="discuss-image-preview" id="discuss-img-preview" style="display:none;">' +
                '<img id="discuss-img-thumb" alt="待发送图片">' +
                '<button type="button" class="discuss-img-cancel" onclick="cancelDiscussImage()">移除图片</button>' +
            '</div>' +
            '<div class="discuss-composer">' +
                '<input type="file" id="discuss-file" accept="image/png,image/jpeg,image/gif,image/webp" style="display:none;" onchange="uploadDiscussImage()">' +
                '<button type="button" class="btn btn-sm" onclick="document.getElementById(\'discuss-file\').click()" title="发送图片">图片</button>' +
                '<input type="text" id="discuss-input" maxlength="1000" placeholder="输入讨论内容，回车发送…" onkeydown="if(event.key===\'Enter\'){sendDiscussMessage();}">' +
                '<button type="button" class="btn btn-sm btn-success" onclick="sendDiscussMessage()">发送</button>' +
            '</div>' +
            '<p class="discuss-hint">支持文字与图片（jpg/png/gif/webp，≤10MB），消息约每 3 秒自动刷新</p>' +
        '</div>';

    const list = document.getElementById('discuss-list');
    list.addEventListener('scroll', () => {
        discussPinned = list.scrollHeight - list.scrollTop - list.clientHeight < 40;
    });

    await loadDiscussMessages(true);
    if (discussTimer) clearInterval(discussTimer);
    discussTimer = setInterval(() => {
        if (document.visibilityState === 'visible') loadDiscussMessages(false);
    }, 3000);
}

async function loadDiscussMessages(forceScroll) {
    const list = document.getElementById('discuss-list');
    if (!list) return;
    let r;
    try {
        r = await api('/api/discuss/messages?limit=50');
    } catch (e) { return; }
    if (r.code !== 0) return;

    list.innerHTML = (r.data.length === 0)
        ? '<div class="discuss-empty">还没有讨论消息，来发第一条吧～</div>'
        : r.data.map(m => renderDiscussMessage(m)).join('');

    if (forceScroll || discussPinned) {
        list.scrollTop = list.scrollHeight;
        discussPinned = true;
    }
}

function renderDiscussMessage(m) {
    const mine = discussUser && m.senderId === discussUser.id;
    const roleName = DISCUSS_ROLE_NAME[m.senderRole] || '用户';
    const time = fmtTime(m.createTime);
    let body = '';
    if (m.content) body += '<div class="discuss-bubble">' + esc(m.content) + '</div>';
    if (m.imageUrl) {
        body += '<a href="' + esc(m.imageUrl) + '" target="_blank" rel="noopener">' +
                '<img class="discuss-img" src="' + esc(m.imageUrl) + '" alt="讨论图片"></a>';
    }
    const head = '<span class="discuss-name">' + esc(m.senderName || '未知用户') + '</span>' +
                 '<span class="discuss-role role-' + (m.senderRole || 0) + '">' + roleName + '</span>' +
                 '<span class="discuss-time">' + time + '</span>';
    return '<div class="discuss-msg ' + (mine ? 'mine' : '') + '">' +
        (mine ? '<div class="discuss-meta">' + head + '</div><div class="discuss-body">' + body + '</div>'
              : '<div class="discuss-meta">' + head + '</div><div class="discuss-body">' + body + '</div>') +
        '</div>';
}

async function sendDiscussMessage() {
    const input = document.getElementById('discuss-input');
    const content = input.value.trim();
    if (!content && !discussPendingImage) { toast('请输入内容或选择图片', 'error'); return; }
    const r = await api('/api/discuss/messages', {
        method: 'POST',
        body: JSON.stringify({ content: content, imageUrl: discussPendingImage })
    });
    if (r.code === 0) {
        input.value = '';
        cancelDiscussImage(true);
        discussPinned = true;
        await loadDiscussMessages(true);
    } else {
        toast(r.message, 'error');
    }
}

async function uploadDiscussImage() {
    const fileInput = document.getElementById('discuss-file');
    const file = fileInput.files && fileInput.files[0];
    if (!file) return;
    if (file.size > 10 * 1024 * 1024) { toast('图片不能超过 10MB', 'error'); fileInput.value = ''; return; }
    const fd = new FormData();
    fd.append('file', file);
    toast('图片上传中…', 'info');
    try {
        const res = await fetch('/api/discuss/upload', {
            method: 'POST',
            headers: { 'Authorization': 'Bearer ' + getToken() },
            body: fd
        });
        const data = await res.json();
        if (data.code === 0) {
            discussPendingImage = data.data.url;
            document.getElementById('discuss-img-thumb').src = data.data.url;
            document.getElementById('discuss-img-preview').style.display = 'flex';
            toast('图片已添加，点击发送', 'success');
        } else {
            toast(data.message, 'error');
        }
    } catch (e) {
        toast('图片上传失败，请确认后端服务已启动', 'error');
    }
    fileInput.value = '';
}

function cancelDiscussImage(silent) {
    discussPendingImage = null;
    const preview = document.getElementById('discuss-img-preview');
    if (preview) preview.style.display = 'none';
    const fileInput = document.getElementById('discuss-file');
    if (fileInput) fileInput.value = '';
    if (!silent) toast('已移除图片', 'info');
}
