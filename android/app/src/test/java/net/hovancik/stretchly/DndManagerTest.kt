package net.hovancik.stretchly

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class DndManagerTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockNotificationManager: NotificationManager

    private lateinit var dndManager: DndManager

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(mockContext.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockNotificationManager)
        dndManager = DndManager(mockContext)
    }

    @Test
    fun testIsDndEnabled_whenFilterIsAll_returnsFalse() {
        `when`(mockNotificationManager.currentInterruptionFilter).thenReturn(NotificationManager.INTERRUPTION_FILTER_ALL)
        assertFalse(dndManager.isDndEnabled())
    }

    @Test
    fun testIsDndEnabled_whenFilterIsPriority_returnsTrue() {
        `when`(mockNotificationManager.currentInterruptionFilter).thenReturn(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
        assertTrue(dndManager.isDndEnabled())
    }

    @Test
    fun testIsDndEnabled_whenFilterIsAlarms_returnsTrue() {
        `when`(mockNotificationManager.currentInterruptionFilter).thenReturn(NotificationManager.INTERRUPTION_FILTER_ALARMS)
        assertTrue(dndManager.isDndEnabled())
    }

    @Test
    fun testIsDndEnabled_whenFilterIsNone_returnsTrue() {
        `when`(mockNotificationManager.currentInterruptionFilter).thenReturn(NotificationManager.INTERRUPTION_FILTER_NONE)
        assertTrue(dndManager.isDndEnabled())
    }
}
