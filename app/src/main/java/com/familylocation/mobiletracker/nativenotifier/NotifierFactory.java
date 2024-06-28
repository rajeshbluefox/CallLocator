package com.familylocation.mobiletracker.nativenotifier;

import java.util.Vector;

public class NotifierFactory {
    
    private Vector< EventNotifier > _eventNotifiers = null;
    
    private static NotifierFactory _notifierFactoryInstance = null;
    
    public static final int EVENT_NOTIFIER_LOCATION_UPDATE = 2;

    public static final int EVENT_NOTIFIER_USER_INFO = 3;
    public static final int EVENT_NOTIFIER_DOWNLOAD_INFO = 4;

    public static final int EVENT_NOTIFIER_AD_STATUS = 4;

    public static final int EVENT_NOTIFIER_DATA_UPDATED = 5;

    public static final int EVENT_NOTIFIER_DATA_LIKE = 6;


    private NotifierFactory( ) {
        _eventNotifiers = new Vector< EventNotifier >( );
    }
    
    public static NotifierFactory getInstance() {
        if ( _notifierFactoryInstance == null ) {
            _notifierFactoryInstance = new NotifierFactory( );
        }
        return _notifierFactoryInstance;
    }
    
    public EventNotifier getNotifier( int eventCategory ) {
        
        EventNotifier eventNotifier = findNotifier( eventCategory );
        if ( eventNotifier != null ) {
            
            return eventNotifier;
        }
        

        EventNotifier objEventNotifier = new EventNotifier( eventCategory );
        _eventNotifiers.addElement( objEventNotifier );
        
        return objEventNotifier;
    }
    
    private EventNotifier findNotifier( int eventCategory ) {
        
        EventNotifier eventNotifierObject = null;
        
        int length = _eventNotifiers.size( );
        
        for ( int index = 0; index < length; index++ ) {
            
            eventNotifierObject = (EventNotifier) _eventNotifiers.elementAt( index );
            int category = eventNotifierObject.getEventCategory( );
            
            if ( eventCategory == category ) {
                
                return eventNotifierObject;
            }
            eventNotifierObject = null;
        }
        return eventNotifierObject;
    }
}