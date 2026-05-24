/**
 *
 * 1. USER MANAGEMENT
 *    - The system shall allow an Admin to create, update, and delete user accounts.
 *    - Each user shall be assigned one role.
 *    - The system shall authenticate users by username and password.
 *
 * 2. TOLL BOOTH MANAGEMENT
 *    - The system shall allow an Admin to add, update and delete toll booths.
 *    - Each booth shall be assigned to one Operator.
 *
 * 3. VEHICLE MANAGEMENT
 *    - The system shall allow a Vehicle Owner to register,update and delete their vehicles.
 *    - Each vehicle shall have a unique plate number and a type (car, truck, motorbike, bus).
 *
 * 4. TOLL PAYMENT 
 *    - The system shall allow an Operator to record payment for a vehicle passing their booth.
 *    - The amount charged will be based on vehicle type.
 *    - Each transaction will be saved to the database with date, booth, vehicle and operator.
 *
 * 5. REPORTING & MONITORING
 *    - The system allows a Supervisor to view all transactions across all booths.
 *    - The system will display the total revenue.
 *
 * 6. MULTI-USER SUPPORT
 *    - The system shall support multiple users connected simultaneously via socket communication.
 *
 * 7. ROLE-BASED ACCESS CONTROL
 *    - Each user will only access features relevant to their responsibilities.
 *    - Admin: full system access.
 *    - Operator: process payments only.
 *    - Supervisor: view reports only.
 *    - Owner: manage own vehicles and view own history.
 * ============================================================
 */
module tollManagement_System {
	requires jdk.jdi;
	requires java.sql;
	requires java.desktop;
}