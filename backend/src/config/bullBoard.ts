import { createBullBoard } from "@bull-board/api";
import { BullMQAdapter } from "@bull-board/api/bullMQAdapter";
import { ExpressAdapter } from "@bull-board/express";
import { notificationQueue } from "../queues/notificationQueue";

export const bullBoardServerAdapter = new ExpressAdapter();
bullBoardServerAdapter.setBasePath("/admin/queues");

createBullBoard({
  queues: [new BullMQAdapter(notificationQueue)],
  serverAdapter: bullBoardServerAdapter,
});
