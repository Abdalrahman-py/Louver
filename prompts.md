# Copilot Instructions – Louver Project

## General Rules

1. Follow MVVM strictly.
2. Never access DAO directly from Fragment.
3. Do not move business logic into UI.
4. Keep booking logic centralized.
5. Preserve current package structure.

---

## When Creating a New Feature

1. Create Entity (if needed).
2. Create DAO methods.
3. Update Repository.
4. Create or update ViewModel.
5. Update Fragment.
6. Update XML layout.

Do not skip repository layer.

---

## When Fixing Bugs

1. Trace UI → ViewModel → Repository → DAO.
2. Do not rewrite entire architecture.
3. Only modify minimal necessary code.

---

## Booking Implementation Rule

When implementing booking:

- Validate time range.
- Calculate days using centralized logic.
- Multiply by daily price.
- Save BookingEntity.
- Schedule notifications.

Never calculate price inside Fragment.

---

## Favorites Rule

Favorites must:

- Be stored in FavoriteEntity.
- Use userId + carId relation.
- Be observable via LiveData.

---

## Notifications Rule

- Schedule alarms when booking is created.
- Cancel alarms if booking is cancelled.
- Respect AppSettingsEntity.notificationsEnabled.

---

## ViewModel Rule

ViewModels must:

- Expose LiveData.
- Not hold Context.
- Not access DAO directly.
- Only talk to Repository.

---

## If Unsure

Ask:
"Which layer should this logic belong to?"
Default answer: Repository.