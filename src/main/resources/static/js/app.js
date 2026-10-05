/* ==========================================================
   Shopify store - shared front-end code
   (API helper, login state, cart, navbar, toasts)
   ========================================================== */

// If you open the HTML files with VS Code Live Server, set this to "http://localhost:8080"
const API = window.API_BASE || '';

/* ---------- small helpers ---------- */
function esc(value) {
  return String(value == null ? '' : value).replace(/[&<>"']/g, function (c) {
    return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
  });
}

function money(n) {
  return '\u20B9' + Number(n).toLocaleString('en-IN');
}

function formatDate(iso) {
  return iso ? new Date(iso).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' }) : '';
}

function toast(message, type) {
  var box = document.getElementById('toastBox');
  if (!box) {
    box = document.createElement('div');
    box.id = 'toastBox';
    box.className = 'toast-box';
    document.body.appendChild(box);
  }
  var el = document.createElement('div');
  el.className = 'alert alert-' + (type || 'success') + ' shadow-sm';
  el.textContent = message;
  box.appendChild(el);
  setTimeout(function () { el.remove(); }, 3000);
}

/* ---------- login state (kept in localStorage) ---------- */
var Auth = {
  get token() { return localStorage.getItem('token'); },
  get user() {
    try { return JSON.parse(localStorage.getItem('user')); } catch (e) { return null; }
  },
  get isLoggedIn() { return !!this.token; },
  get isAdmin() { return !!this.user && this.user.role === 'ADMIN'; },
  save: function (data) {
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify({ name: data.name, email: data.email, role: data.role }));
  },
  clear: function () {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
  logout: function () {
    this.clear();
    window.location.href = 'index.html';
  }
};

/* ---------- REST API helper ---------- */
async function api(path, options) {
  options = options || {};
  var headers = { 'Content-Type': 'application/json' };
  if (Auth.token) headers['Authorization'] = 'Bearer ' + Auth.token;

  var res = await fetch(API + path, {
    method: options.method || 'GET',
    headers: headers,
    body: options.body ? JSON.stringify(options.body) : undefined
  });

  var text = await res.text();
  var data = null;
  if (text) {
    try { data = JSON.parse(text); } catch (e) { data = text; }
  }

  if (!res.ok) {
    if (res.status === 401 && Auth.token) {
      Auth.clear();
      throw new Error('Your session expired. Please log in again.');
    }
    if (res.status === 401) throw new Error('Please log in first.');
    if (res.status === 403) throw new Error('You are not allowed to do this.');
    throw new Error((data && data.message) || 'Something went wrong');
  }
  return data;
}

/* ---------- shopping cart (kept in localStorage until checkout) ---------- */
var Cart = {
  items: function () {
    try { return JSON.parse(localStorage.getItem('cart')) || []; } catch (e) { return []; }
  },
  save: function (items) {
    localStorage.setItem('cart', JSON.stringify(items));
    updateCartBadge();
  },
  add: function (product, qty) {
    var items = this.items();
    var found = items.find(function (i) { return i.id === product.id; });
    if (found) {
      found.qty += qty || 1;
    } else {
      items.push({ id: product.id, name: product.name, price: product.price, imageUrl: product.imageUrl, qty: qty || 1 });
    }
    this.save(items);
  },
  setQty: function (id, qty) {
    var items = this.items();
    items.forEach(function (i) { if (i.id === id) i.qty = Math.max(1, qty); });
    this.save(items);
  },
  remove: function (id) {
    this.save(this.items().filter(function (i) { return i.id !== id; }));
  },
  clear: function () { this.save([]); },
  count: function () {
    return this.items().reduce(function (sum, i) { return sum + i.qty; }, 0);
  },
  total: function () {
    return this.items().reduce(function (sum, i) { return sum + i.price * i.qty; }, 0);
  }
};

function updateCartBadge() {
  var el = document.getElementById('cartCount');
  if (el) el.textContent = Cart.count();
}

/* ---------- navbar (same on every page) ---------- */
function renderNavbar(active, withSearch) {
  var user = Auth.user;
  var authLinks;
  if (user) {
    authLinks =
      (Auth.isAdmin ? '<li class="nav-item"><a class="nav-link" href="admin.html">Admin</a></li>' : '') +
      '<li class="nav-item"><a class="nav-link" href="orders.html">My Orders</a></li>' +
      '<li class="nav-item"><span class="nav-link text-muted">Hi, ' + esc(user.name.split(' ')[0]) + '</span></li>' +
      '<li class="nav-item"><a class="nav-link" href="#" id="logoutLink">Logout</a></li>';
  } else {
    authLinks = '<li class="nav-item"><a class="nav-link" href="login.html">Login / Register</a></li>';
  }

  var searchForm = withSearch
    ? '<form class="form-inline my-2 my-lg-0 mr-lg-2" id="searchForm">' +
      '<input class="form-control mr-sm-2" type="search" id="searchInput" placeholder="Search" aria-label="Search">' +
      '<button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button></form>'
    : '';

  function cls(name) { return 'nav-link' + (active === name ? ' font-weight-bold' : ''); }

  document.getElementById('navbar').innerHTML =
    '<nav class="navbar navbar-expand-lg fixed-top site-nav">' +
    '<a class="navbar-brand" href="index.html" id="logo">' +
    '<img src="images/logo.png" alt="shopify store" width="30" height="24">' +
    '<span id="span1">S</span><span>hopi</span><span id="span2">fy</span> <span id="span4">st</span><span>o</span><span id="span5">Re</span></a>' +
    '<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navMenu" aria-controls="navMenu" aria-expanded="false" aria-label="Toggle navigation"><span class="navbar-toggler-icon"></span></button>' +
    '<div class="collapse navbar-collapse" id="navMenu">' +
    '<ul class="navbar-nav mr-auto">' +
    '<li class="nav-item"><a class="' + cls('home') + '" href="index.html">Home</a></li>' +
    '<li class="nav-item"><a class="' + cls('shop') + '" href="index.html#products">Shop</a></li>' +
    '<li class="nav-item"><a class="' + cls('contact') + '" href="ContactUs.html">Contact us</a></li>' +
    '</ul>' + searchForm +
    '<ul class="navbar-nav">' +
    '<li class="nav-item"><a class="' + cls('cart') + '" href="cart.html">Cart <span class="cart-badge" id="cartCount">0</span></a></li>' +
    authLinks + '</ul></div></nav>';

  var logout = document.getElementById('logoutLink');
  if (logout) logout.addEventListener('click', function (e) { e.preventDefault(); Auth.logout(); });
  updateCartBadge();
}

function renderFooter() {
  var el = document.getElementById('footer');
  if (el) el.innerHTML = '<footer class="site-footer">&copy; ' + new Date().getFullYear() + ' Shopify store &middot; Built with Java, Spring Boot &amp; MySQL</footer>';
}
