package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.Appointment
import com.example.model.AppointmentStatus
import com.example.model.AppointmentType
import com.example.model.FinanceTransaction
import com.example.model.Priority
import com.example.model.TaskItem
import com.example.model.TaskStatus
import com.example.model.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TaskItem::class, Appointment::class, FinanceTransaction::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun businessDao(): BusinessDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "business_manager_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialBusinessData(dao: BusinessDao) {
            val now = System.currentTimeMillis()
            val day = 86400000L

            // Initial Sample Tasks
            val sampleTasks = listOf(
                TaskItem(
                    title = "مراجعة العقد وتوقيع الشراكة التجارية",
                    description = "دراسة البنود المالية والقانونية وتجهيز النسخة النهائية للتوقيع.",
                    priority = Priority.URGENT,
                    status = TaskStatus.IN_PROGRESS,
                    projectName = "شركة الأفق للاستثمار",
                    deadline = now + day,
                    dueDate = now + day,
                    clientOrProject = "شركة الأفق للاستثمار"
                ),
                TaskItem(
                    title = "إعداد تقرير الأرباح الربع سنوي Q3",
                    description = "حساب إجمالي المبيعات، عوائد الإعلانات، والمصروفات التشغيلية.",
                    priority = Priority.HIGH,
                    status = TaskStatus.PENDING,
                    projectName = "الإدارة المالية",
                    deadline = now + (day * 2),
                    dueDate = now + (day * 2),
                    clientOrProject = "الإدارة المالية"
                ),
                TaskItem(
                    title = "إطلاق الحملة الإعلانية الرقمية الجديدة",
                    description = "تجهيز البانرات وتحديد الميزانية واستهداف العملاء المحتملين.",
                    priority = Priority.MEDIUM,
                    status = TaskStatus.PENDING,
                    projectName = "فريق التسويق",
                    deadline = now + (day * 3),
                    dueDate = now + (day * 3),
                    clientOrProject = "فريق التسويق"
                ),
                TaskItem(
                    title = "تحديث خطة نمو المشاريع وتوزيع الميزانية",
                    description = "مواءمة الأهداف التشغيلية مع خطة زيادة الأرباح بنسبة 25%.",
                    priority = Priority.LOW,
                    status = TaskStatus.COMPLETED,
                    projectName = "مجلس الإدارة",
                    deadline = now - day,
                    dueDate = now - day,
                    clientOrProject = "مجلس الإدارة"
                )
            )
            sampleTasks.forEach { dao.insertTask(it) }

            // Initial Sample Appointments
            val sampleAppointments = listOf(
                Appointment(
                    title = "جلسة استراتيجية وتفاوض مالي",
                    clientName = "المهندس خالد العتيبي",
                    dateTime = now + (3600000L * 4), // after 4 hours
                    durationMinutes = 60,
                    locationOrLink = "برج الفيصلية - القاعة الرئيسية",
                    type = AppointmentType.IN_PERSON,
                    status = AppointmentStatus.CONFIRMED,
                    notes = "مناقشة توسع الخدمات واستثمار رأس المال الجديد.",
                    dealValue = 45000.0
                ),
                Appointment(
                    title = "اجتماع عرض خطة العمل والتسعير",
                    clientName = "د. سارة المنصور",
                    dateTime = now + day + (3600000L * 2),
                    durationMinutes = 45,
                    locationOrLink = "https://meet.google.com/biz-mgmt-growth",
                    type = AppointmentType.VIDEO_CALL,
                    status = AppointmentStatus.SCHEDULED,
                    notes = "تقديم العرض النهائي لخدمات الاستشارات وإدارة المشاريع.",
                    dealValue = 18500.0
                ),
                Appointment(
                    title = "مكالمة متابعة تنفيذ المشروع",
                    clientName = "أحمد السالم (مؤسسة الرواد)",
                    dateTime = now + (day * 2) + 3600000L,
                    durationMinutes = 30,
                    locationOrLink = "+966500000000",
                    type = AppointmentType.PHONE_CALL,
                    status = AppointmentStatus.SCHEDULED,
                    notes = "تأكيد موعد تسليم المرحلة الأولى واستلام الدفعة الثانية.",
                    dealValue = 9200.0
                )
            )
            sampleAppointments.forEach { dao.insertAppointment(it) }

            // Initial Sample Financial Records
            val sampleFinance = listOf(
                FinanceTransaction(
                    title = "دفعة استشارات إدارية وتطوير أعمال",
                    amount = 12500.0,
                    type = TransactionType.INCOME,
                    category = "استشارات",
                    date = now - (day * 1)
                ),
                FinanceTransaction(
                    title = "أرباح إعلانات AdMob الشهرية",
                    amount = 3450.0,
                    type = TransactionType.INCOME,
                    category = "إعلانات أدموب",
                    date = now - (day * 2)
                ),
                FinanceTransaction(
                    title = "إيرادات عقد إدارة مشاريع",
                    amount = 28000.0,
                    type = TransactionType.INCOME,
                    category = "مشاريع",
                    date = now - (day * 4)
                ),
                FinanceTransaction(
                    title = "اشتراكات برمجيات وأدوات سحابية",
                    amount = 1200.0,
                    type = TransactionType.EXPENSE,
                    category = "برمجيات",
                    date = now - (day * 3)
                ),
                FinanceTransaction(
                    title = "حملات تسويقية وترويجية",
                    amount = 2600.0,
                    type = TransactionType.EXPENSE,
                    category = "تسويق",
                    date = now - (day * 5)
                )
            )
            sampleFinance.forEach { dao.insertTransaction(it) }
        }
    }
}
