# Guía de Despliegue en Kubernetes (Evaluación APIs IA)

Este documento detalla el paso a paso necesario para levantar el proyecto localmente y presentarlo en la evaluación.

---

## 🚀 Requisitos Previos

Asegúrate de que la máquina donde se evaluará cuente con:
1.  **Docker Desktop** (con Kubernetes habilitado) o un clúster local configurado.
2.  **Git** instalado.
3.  **Acceso a Internet** (para conectar con Neon DB y Pollinations.ai).

---

## 🛠️ Paso 1: Clonar y Construir Imágenes Locales

Si clonas el repositorio en una máquina nueva, primero debes situarte en las carpetas y construir las imágenes locales para que Kubernetes pueda consumirlas.

### 1. Construir la imagen del Backend (API Spring Boot)
```powershell
cd AS241S5_AEJ_13-be
docker build -t dariohuamanconca/as241s5_aej_13-be:latest .
```

### 2. Construir la imagen del Frontend (Vite + React + Nginx)
```powershell
cd ../AS241S5_AEJ_13-fe
docker build -t dariohuamanconca/as241s5_aej_13-fe:latest .
```

---

## ☸️ Paso 2: Despliegue en Kubernetes

Desde la raíz de la carpeta del proyecto (donde se encuentra la carpeta `/k8s`), ejecuta los siguientes comandos en orden para aplicar los manifiestos:

```powershell
# 1. Crear el espacio de nombres (Namespace)
kubectl apply -f k8s/namespace.yml

# 2. Registrar las variables y endpoints del ConfigMap (Neon DB, RapidAPI y endpoints de IA)
kubectl apply -f k8s/configmap.yml

# 3. Levantar los despliegues (Deployments)
kubectl apply -f k8s/deployment.yml

# 4. Exponer los servicios (Services)
kubectl apply -f k8s/service.yml
```

---

## 🔍 Paso 3: Verificar que todo esté OK

Ejecuta el siguiente comando para revisar que los Pods pasen a estado **`Running 1/1`** y los servicios estén listos:

```powershell
kubectl get all -n ia-project
```

*Nota: Si modificas el archivo `configmap.yml` en caliente, puedes forzar el reinicio de los contenedores usando:*
```powershell
kubectl rollout restart deployment backend-deployment -n ia-project
kubectl rollout restart deployment frontend-deployment -n ia-project
```

---

## 🌐 Paso 4: Probar la Aplicación en la Evaluación

Abre tu navegador de internet y accede a:

👉 **`http://localhost:30080`**

### ¿Qué probar en la demo?
1.  **Llama / Qwen (Texto):** Envía un mensaje de chat tradicional (usa tu RapidAPI Key).
2.  **Imagen (IA):** Selecciona la pestaña **"Imagen (IA)"** en el menú inferior, escribe un prompt (ejemplo: *"un perro jugando con una pelota"*) y presiona **"Generar Imagen"**. La imagen se generará y se mostrará de forma directa en el chat usando Pollinations.ai sin límites ni bloqueos.
3.  **Neon DB (Base de datos):** Todas las consultas e imágenes generadas se guardan de forma persistente en tu base de datos Neon DB en la nube.
