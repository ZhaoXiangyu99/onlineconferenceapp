package com.dds.skywebrtc;


public class EnumType {

    public enum CallState {
        Idle,
        Outgoing,
        Incoming,
        Connecting,
        Connected;

        CallState() {
        }
    }

    public enum CallEndReason {
        Busy,
        SignalError,
        RemoteSignalError,
        Hangup,
        MediaError,
        RemoteHangup,
        OpenCameraFailure,
        Timeout,
        AcceptByOtherClient;

        CallEndReason() {
        }
    }

    public enum RefuseType {
        Busy,
        Hangup,
    }


}
