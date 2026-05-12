import app from "./app";
import { PORT } from "./config/env";
import { startNotificationWorker } from "./workers/notificationWorker";

const start = () => {
  startNotificationWorker();
  app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
  });
};

start();
