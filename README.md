# Pokedex - PokeUI

## Pasos para habilitar Swing en caso de error

Si encuentras problemas al intentar usar Swing, sigue estos pasos para habilitarlo correctamente:

### 1. Navegar hasta "Edit Configuration"
Dirígete a la configuración de tu IDE y selecciona la opción "Edit Configuration".

![image](https://github.com/user-attachments/assets/9e704ea5-56b0-4310-ab47-cb5b29956537)

### 2. Haz clic en "Modify Options" y marca "Add VM Options"
Dentro de la configuración, haz clic en "Modify Options" y selecciona "Add VM Options".

![image](https://github.com/user-attachments/assets/935152bb-3e73-4103-a9cc-c9c15668af0a)

![image](https://github.com/user-attachments/assets/96590959-d7f6-45a1-ac8a-03ed9316ccb7)

### 3. Ingresa el comando `-Djava.awt.headless=false`
Finalmente, agrega el siguiente comando en el campo de opciones VM:

-Djava.awt.headless=false

![image](https://github.com/user-attachments/assets/5d5dbf57-40ba-47e9-95a2-f49d27f5b327)

---

Ahora debería funcionar correctamente.
