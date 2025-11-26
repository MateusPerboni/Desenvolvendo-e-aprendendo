# 📱 Registro de Carros - Web + App

> Aplicação completa para gerenciar compras de carros com sincronização entre Web e App Android

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue)
![Android](https://img.shields.io/badge/Android-14-green)
![Material Design](https://img.shields.io/badge/Material-Design%203-purple)
![PHP](https://img.shields.io/badge/PHP-7.4+-yellow)
![Status](https://img.shields.io/badge/Status-Completo-brightgreen)

---

## 📋 Descrição

Sistema de gerenciamento de carros com:
- ✅ Web responsivo com PHP + MySQL
- ✅ App Android 100% Kotlin
- ✅ Material Design 3
- ✅ Busca em tempo real
- ✅ Tema claro/escuro automático
- ✅ CRUD completo integrado

---

## 🚀 Quick Start

### Web
```bash
# Abrir em navegador
http://localhost/P2/RegistroCarrosWeb/

# Ou criar conta e fazer login
```

### App
```bash
# Compilar
cd P2/RegistroCarrosApp
./gradlew build

# Instalar
./gradlew installDebug

# Rodar
./gradlew run
```

---

## 🎯 Funcionalidades

### Autenticação
- [x] Login com email/senha
- [x] Registro de novo usuário
- [x] Logout seguro
- [x] Validação de entrada

### Carros (CRUD)
- [x] Listar todos os carros
- [x] **Pesquisar** em tempo real
- [x] Adicionar novo carro
- [x] Editar carro existente
- [x] Deletar carro
- [x] Confirmações de ação

### Interface
- [x] Material Design 3
- [x] Tema light/dark automático
- [x] Responsivo (mobile + desktop)
- [x] Loading overlays
- [x] Mensagens de erro claras

---

## 📊 Campos de Carro

| Campo | Tipo | Exemplo |
|-------|------|---------|
| Marca | Text | Toyota |
| Modelo | Text | Corolla |
| Ano | Número | 2022 |
| Cor | Text | Azul |
| Preço | Decimal | 85.000,00 |
| Data Compra | Data | 15/06/2023 |
| Observações | Texto | Seminovo |

---

## 🎨 Screenshots

### Web
```
┌─────────────────────────────────┐
│ RC    [🌙]  [Sair]              │
├─────────────────────────────────┤
│ Registro de Compras de Carros    │
│                                  │
│ [Buscar...]                      │
│ ┌──────────────────────────────┐ │
│ │ Adicionar Novo Carro         │ │
│ │ Marca: [______]              │ │
│ │ Modelo: [______]             │ │
│ │ ...                          │ │
│ │ [Registrar Carro]            │ │
│ └──────────────────────────────┘ │
│                                  │
│ Carros Registrados               │
│ ┌────────────────┐              │
│ │ Toyota Corolla │ [Ed] [Del]   │
│ │ Ano: 2022      │              │
│ │ R$ 85.000      │              │
│ └────────────────┘              │
└─────────────────────────────────┘
```

### App
```
┌─────────────────────────────────┐
│ Registro de Carros     [Sair]    │
├─────────────────────────────────┤
│ [Buscar...]                      │
│ [+ Adicionar Novo Carro]         │
├─────────────────────────────────┤
│ ┌────────────────────────────┐  │
│ │ Toyota Corolla             │  │
│ │ Ano: 2022                  │  │
│ │ Cor: Azul                  │  │
│ │ R$ 85.000,00               │  │
│ │ Compra: 15/06/2023         │  │
│ │ [Editar] [Excluir]         │  │
│ └────────────────────────────┘  │
│ ┌────────────────────────────┐  │
│ │ Honda Civic                │  │
│ │ ...                        │  │
│ └────────────────────────────┘  │
└─────────────────────────────────┘
```

---

## 📁 Estrutura do Projeto

```
P2/
├── RegistroCarrosWeb/          # Backend Web
│   ├── backend/api/
│   │   ├── usuarios.php        # API usuários
│   │   └── carros.php          # API carros ⭐
│   ├── css/style.css           # Estilos (Material Design 3)
│   ├── index.php               # Dashboard principal
│   ├── login.php               # Tela login
│   ├── register.php            # Tela registro
│   └── conexao.php             # Conexão DB
│
├── RegistroCarrosApp/          # App Android
│   └── app/src/main/
│       ├── java/.../
│       │   ├── LoginActivity.kt
│       │   ├── RegisterActivity.kt
│       │   ├── MainActivity.kt
│       │   ├── CarroFormActivity.kt
│       │   ├── adapter/
│       │   ├── model/
│       │   └── network/
│       └── res/
│           ├── layout/         # 6 arquivos XML
│           ├── values/         # Cores, temas
│           └── values-night/   # Dark mode
│
└── 📄 Documentação
    └── README.md (este arquivo)
```

---

## 🔧 Stack Tecnológico

### Web
- **Backend:** PHP 7.4+
- **Database:** MySQL
- **Frontend:** HTML5, CSS3, JavaScript
- **Design:** Material Design 3

### App Android
- **Linguagem:** Kotlin 1.9.20
- **API:** Android 14 (API 34)
- **Design:** Material Design 3
- **Networking:** Retrofit 2.9
- **JSON:** GSON 2.10
- **UI:** ViewBinding, RecyclerView

---

## 🎓 Padrões Utilizados

### App Android
- **MVVM-lite:** Activities + ViewModel concepts
- **Repository Pattern:** ApiClient + Retrofit
- **Observer Pattern:** LiveData (opcional)
- **Adapter Pattern:** RecyclerView Adapter
- **Builder Pattern:** JsonObject, Retrofit Builder

### Web
- **MVC:** Model (DB) + View (HTML) + Controller (PHP)
- **RESTful API:** JSON endpoints
- **Prepared Statements:** SQL injection prevention

---

## 🔐 Segurança

### Implementado
- ✅ Prepared statements (SQL injection prevention)
- ✅ Password hashing (PASSWORD_DEFAULT)
- ✅ Email validation
- ✅ Input sanitization
- ✅ CORS headers
- ✅ Cleartext traffic allowed (HTTP, pode usar HTTPS)

### Recomendado Futuramente
- JWT tokens
- Refresh tokens
- Rate limiting
- HTTPS obrigatório
- 2FA

---

## 📱 Requisitos

### Web
- PHP 7.4 ou superior
- MySQL 5.7 ou superior
- XAMPP/similar
- Navegador moderno

### App
- Android 6.0+ (API 24)
- 50MB espaço livre
- Conexão com internet
- Emulador ou device real

---

## 🚀 Deployment

### Web
1. Copiar arquivos para servidor
2. Criar banco de dados
3. Ajustar `conexao.php`
4. Testar endpoints

---

## 🤝 Contribuindo

Para melhorar o projeto:

1. Fork o repositório
2. Crie uma branch (`git checkout -b feature/melhoria`)
3. Commit suas mudanças (`git commit -m 'Add melhoria'`)
4. Push para a branch (`git push origin feature/melhoria`)
5. Abra um Pull Request

---

## 📄 Licença

Este projeto é fornecido como está. Sinta-se livre para usar, modificar e distribuir.

---

## 🎉 Conclusão

Aplicação com:
- ✨ Interface moderna (Material Design 3)
- 🔄 Sincronização Web ↔ App
- 🔍 Busca inteligente em tempo real
- 🎯 CRUD completo funcional
- 📱 100% responsivo
- 🌙 Tema automático claro/escuro
- 🔒 Seguro e validado

**Pronto para uso e desenvolvimento!** 🚀

---

**Desenvolvido em Kotlin e PHP**

Data: 26 de Novembro de 2025

Version: 1.1.0

Projeto finalizado para entrega!!!