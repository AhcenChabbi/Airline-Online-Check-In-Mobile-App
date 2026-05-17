import { Router } from "express";
import { requireAuth } from "../middleware/auth";
import { registerFcmToken } from "../controllers/user.controller";

const router = Router();

router.get("/me", requireAuth, (req, res) => {
  res.json({ user: req.user });
});

router.post("/fcm-token", requireAuth, registerFcmToken);

export default router;
