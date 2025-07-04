    package com.patrimoine.backend.controller;

    import com.patrimoine.backend.entity.Equipment;
    import com.patrimoine.backend.service.EquipmentService;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Map;
    import java.util.Optional;

    @RestController
    @RequestMapping("/api/equipments")
    @CrossOrigin(origins = "http://localhost:3000")
    public class EquipmentController {

        private final EquipmentService equipmentService;

        public EquipmentController(EquipmentService equipmentService) {
            this.equipmentService = equipmentService;
        }

        @GetMapping
        public ResponseEntity<List<Equipment>> getEquipmentsByCenter(
                @RequestHeader("X-User-Center") String userCenter,
                @RequestHeader("X-User-Role") String userRole) {

            List<Equipment> equipments;
            if ("ADMIN".equals(userRole)) {
                equipments = equipmentService.getAllEquipments();
            } else {
                equipments = equipmentService.getValidatedEquipmentsByCenter(userCenter);
            }
            return ResponseEntity.ok(equipments);
        }

        @PostMapping("/add")
        public ResponseEntity<Equipment> addEquipment(
                @RequestBody Equipment equipment,
                @RequestHeader("X-User-Center") String userCenter,
                @RequestHeader("X-User-Email") String userEmail,
                @RequestHeader("X-User-Name") String userName) {

            if (userCenter == null || userEmail == null || userName == null) {
                return ResponseEntity.badRequest().build();
            }

            equipment.setVilleCentre(userCenter);
            equipment.setValidated(false);
            equipment.setAddedBy(userEmail);
            equipment.setAddedByName(userName);
            equipment.setEnMaintenance(false);

            Equipment savedEquipment = equipmentService.addEquipment(equipment);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEquipment);
        }

        @GetMapping("/validated")
        public ResponseEntity<List<Equipment>> getValidatedEquipments(
                @RequestHeader("X-User-Center") String userCenter,
                @RequestHeader("X-User-Role") String userRole) {

            List<Equipment> equipments;
            if ("RESPONSABLE_PATRIMOINE".equals(userRole) || "ADMIN".equals(userRole)) {
                equipments = equipmentService.getValidatedEquipments();
            } else {
                equipments = equipmentService.getValidatedEquipmentsByCenter(userCenter);
            }
            return ResponseEntity.ok(equipments);
        }

        @GetMapping("/pending")
        public ResponseEntity<List<Equipment>> getPendingEquipments(
                @RequestHeader("X-User-Role") String userRole) {

            if (!"RESPONSABLE_PATRIMOINE".equals(userRole) && !"ADMIN".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<Equipment> equipments = equipmentService.getPendingEquipments();
            return ResponseEntity.ok(equipments);
        }

        @GetMapping("/my-equipments")
        public ResponseEntity<List<Equipment>> getMyEquipments(
                @RequestHeader("X-User-Email") String userEmail) {

            List<Equipment> equipments = equipmentService.getEquipmentsByAddedBy(userEmail);
            return ResponseEntity.ok(equipments);
        }

        @PutMapping("/validate/{id}")
        public ResponseEntity<Equipment> validateEquipment(
                @PathVariable Long id,
                @RequestHeader("X-User-Role") String userRole) {

            if (!"RESPONSABLE_PATRIMOINE".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Optional<Equipment> updatedEquipment = equipmentService.validateEquipment(id);
            return updatedEquipment.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @GetMapping("/{id}")
        public ResponseEntity<Equipment> getEquipmentById(@PathVariable Long id) {
            Optional<Equipment> equipment = equipmentService.getEquipmentById(id);
            return equipment.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @PutMapping("/update/{id}")
        public ResponseEntity<Equipment> updateEquipment(
                @PathVariable Long id,
                @RequestBody Equipment equipment,
                @RequestHeader("X-User-Email") String userEmail,
                @RequestHeader("X-User-Role") String userRole) {

            Optional<Equipment> existing = equipmentService.getEquipmentById(id);
            if (existing.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            if (!existing.get().getAddedBy().equals(userEmail) &&
                    !"RESPONSABLE_PATRIMOINE".equals(userRole) &&
                    !"ADMIN".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            equipment.setVilleCentre(existing.get().getVilleCentre());
            equipment.setValidated(existing.get().isValidated());
            equipment.setAddedBy(existing.get().getAddedBy());
            equipment.setAddedByName(existing.get().getAddedByName());
            equipment.setEnMaintenance(existing.get().isEnMaintenance());

            Optional<Equipment> updatedEquipment = equipmentService.updateEquipment(id, equipment);
            return updatedEquipment.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @DeleteMapping("/delete/{id}")
        public ResponseEntity<Void> deleteEquipment(
                @PathVariable Long id,
                @RequestHeader("X-User-Email") String userEmail,
                @RequestHeader("X-User-Role") String userRole) {

            Optional<Equipment> equipment = equipmentService.getEquipmentById(id);
            if (equipment.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            if (!equipment.get().getAddedBy().equals(userEmail) &&
                    !"RESPONSABLE_PATRIMOINE".equals(userRole) &&
                    !"ADMIN".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            boolean deleted = equipmentService.deleteEquipment(id);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        }

        @GetMapping("/responsable/all")
        public ResponseEntity<List<Equipment>> getAllEquipmentsForResponsable(
                @RequestHeader("X-User-Role") String userRole) {

            if (!"RESPONSABLE_PATRIMOINE".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<Equipment> equipments = equipmentService.getAllEquipments();
            return ResponseEntity.ok(equipments);
        }

        @PutMapping("/{id}/maintenance")
        public ResponseEntity<Equipment> mettreEnMaintenance(
                @PathVariable Long id,
                @RequestHeader("X-User-Role") String userRole) {

            if (!"TECHNICIEN".equals(userRole) && !"RESPONSABLE_PATRIMOINE".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Optional<Equipment> equipment = equipmentService.mettreEnMaintenance(id);
            return equipment.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @PutMapping("/{id}/fin-maintenance")
        public ResponseEntity<Equipment> sortirDeMaintenance(
                @PathVariable Long id,
                @RequestHeader("X-User-Role") String userRole) {

            if (!"TECHNICIEN".equals(userRole) && !"RESPONSABLE_PATRIMOINE".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Optional<Equipment> equipment = equipmentService.sortirDeMaintenance(id);
            return equipment.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @GetMapping("/maintenance")
        public ResponseEntity<List<Equipment>> getEquipementsEnMaintenance(
                @RequestHeader("X-User-Center") String userCenter,
                @RequestHeader("X-User-Role") String userRole) {

            if (!"TECHNICIEN".equals(userRole) && !"RESPONSABLE_PATRIMOINE".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<Equipment> equipments = equipmentService.getEquipementsEnMaintenanceByCentre(userCenter);
            return ResponseEntity.ok(equipments);
        }

        @GetMapping("/directeur/all")
        public ResponseEntity<List<Equipment>> getAllEquipmentsForDirector(
                @RequestHeader("X-User-Role") String userRole) {

            if (!"DIRECTEUR".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<Equipment> equipments = equipmentService.getAllEquipmentsForDirector();
            return ResponseEntity.ok(equipments);
        }

        @GetMapping("/directeur/historique/{equipmentId}")
        public ResponseEntity<Map<String, Object>> getEquipmentHistoryForDirector(
                @PathVariable Long equipmentId,
                @RequestHeader("X-User-Role") String userRole) {

            if (!"DIRECTEUR".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Map<String, Object> history = equipmentService.getEquipmentHistoryForDirector(equipmentId);
            return ResponseEntity.ok(history);
        }

    }