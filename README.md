<h1 align="center">🌤️ Weather API</h1>

<p align="center">
API desenvolvida como parte de um <b>desafio técnico</b>, com o objetivo de <b>consumir dados climáticos da OpenWeather API</b>, 
armazenar as informações em um <b>banco PostgreSQL</b> e disponibilizar endpoints REST documentados via <b>Swagger UI</b>.
</p>

---

## 🚀 Tecnologias Utilizadas

🟩 **Backend**
- Java 17 (Eclipse Temurin JDK)
- Spring Boot 3
- Spring Data JPA
- Spring Cloud OpenFeign

🗄️ **Banco de Dados**
- PostgreSQL

🐳 **Infraestrutura**
- Docker & Docker Compose

☁️ **Integração Externa**
- OpenWeather API

📘 **Documentação**
- Swagger / OpenAPI 3 (springdoc-openapi)

---

## 🧩 Funcionalidades

✅ Consultar dados climáticos em tempo real por nome da cidade  
✅ Salvar automaticamente as informações no banco de dados  
✅ Retornar histórico de consultas realizadas  
✅ Documentação interativa via Swagger UI

---

## 🏗️ Estrutura do Projeto

weather-api/

│
├── 📁 src/

│ └── 📁 main/

│ ├── 📁 java/com/gntech/challenge/weatherapi/

│ │ ├── 📂 controller/ # Endpoints REST

│ │ ├── 📂 service/ # Lógica de negócio e integração com OpenWeather (Feign)

│ │ ├── 📂 repository/ # Repositórios JPA

│ │ ├── 📂 entity/ # Entidades do banco de dados (WeatherEntity)

│ │ ├── 📂 dto/ # Data Transfer Objects (WeatherDTO, etc.)

│ │ └── 📂 exception/ # Exceções personalizadas

│ │
│ └── 📁 resources/

│ └── application.yml

│
├── 🐋 Dockerfile

├── 🐳 docker-compose.yml

└── 📄 pom.xml





---

## ⚙️ Configuração do Arquivo `.env`

Crie um arquivo `.env` na raiz do projeto com as variáveis abaixo:

```bash
OPENWEATHER_API_KEY=sua_chave_aqui
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=weather
```
#### 🔗 Obtenha sua chave gratuita em https://openweathermap.org/api

## 🐳 Executando com Docker Compose

#### 1️⃣ Subir os containers

comandos:
```
git clone https://github.com/seu-usuario/weather-api.git
cd weather-api
cp .env.example .env
docker compose up --build
```
Isso irá iniciar:

| Serviço | Descrição | Porta |
|:--------|:-----------|:------:|
| 🐘 **weather-db** | Banco de dados **PostgreSQL** | `5432` |
| 🌤️ **weather-api** | Aplicação **Spring Boot** (API REST) | `8080` |

#### 2️⃣ Acessar a aplicação

Swagger UI:
👉 http://localhost:8080/swagger-ui.html

ou
👉 http://localhost:8080/swagger-ui/index.html

Endpoint de saúde (Actuator):
👉 http://localhost:8080/actuator/health

#### 3️⃣ Parar os containers
```
docker compose down
```
Para limpar volumes (dados do banco):
```
docker compose down -v
```
## 💻 Executando Localmente (sem Docker)

#### 1️⃣ Configure o banco local:

```
spring.datasource.url=jdbc:postgresql://localhost:5433/weather_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

#### 2️⃣ Rode a aplicação:
```
mvn spring-boot:run
```
---
## 🌐 Exemplo de Requisição

#### GET /weather?city=Florianopolis

{
"city": "Florianopolis",

"country": "BR",

"temperature": 26.5,

"description": "céu limpo",

"humidity": 60,

"windSpeed": 4.3,

"timestamp": "2025-11-07T17:20:46.874305911"
}

---
### 🗃️ Estrutura da Tabela (Banco de Dados)

A aplicação persiste os dados climáticos no banco **PostgreSQL**, conforme o modelo abaixo:

| Campo           | Tipo            | Descrição                            |
|-----------------|-----------------|--------------------------------------|
| **id**           | Long            | Identificador único da consulta      |
| **city**         | String          | Nome da cidade consultada            |
| **country**      | String          | Código do país (ex: BR, US)          |
| **temperature**  | Double          | Temperatura atual em graus Celsius   |
| **humidity**     | Double          | Umidade relativa do ar (%)           |
| **windSpeed**    | Double          | Velocidade do vento (m/s)            |
| **description**  | String          | Condição climática (ex: céu limpo)   |
| **timestamp**    | LocalDateTime   | Data e hora em que o dado foi salvo  |

---
##🧪 Executando Testes (opcional)

Para rodar testes unitários:
```
./mvnw test
```
---
## 🧠 Detalhes do Desafio Técnico
O objetivo do desafio foi desenvolver uma aplicação capaz de:


🌍 Consumir a API externa do OpenWeather


💾 Persistir os dados meteorológicos em um banco PostgreSQL


📘 Documentar todos os endpoints com Swagger/OpenAPI


🐳 Permitir execução completa via Docker Compose



---
## 👩‍💻 Autora
### María Belén Caldez
### Desenvolvedora Java Backend 💻
🔗 LinkedIn (https://www.linkedin.com/in/mariabelencaldez/)

📄 Licença
Este projeto foi desenvolvido para fins de avaliação técnica e aprendizado.
Distribuído livremente sob a licença MIT.

---