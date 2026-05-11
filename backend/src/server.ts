import app from "./app";
import { PORT } from "./config/env";
import { startNotificationWorker } from "./workers/notificationWorker.js";

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});

startNotificationWorker();
