import java.time.LocalDateTime

def getSegmentedTime() {
    LocalDateTime now = LocalDateTime.now()
    
    Map timeData = [
        year: now.getYear(),
        month: now.getMonthValue(),
        day: now.getDayOfMonth(),
        hour: now.getHour(),
        minute: now.getMinute(),
        second: now.getSecond(),
        nano: now.getNano(),
        dayOfWeek: now.getDayOfWeek().toString(),
        timestamp: System.currentTimeMillis()
    ]
    
    return [timeData: timeData]
}

Map result = getSegmentedTime()

// Pour le mode service (JSON)
return result
