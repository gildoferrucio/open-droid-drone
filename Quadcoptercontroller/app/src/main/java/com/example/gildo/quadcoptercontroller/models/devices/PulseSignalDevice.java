package com.example.gildo.quadcoptercontroller.models.devices;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PulseInput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by gildo on 19/04/16.
 */
public abstract class PulseSignalDevice {
    private PulseInput pulseInput;

    private PwmOutput pwmOutput;

    private IOIO ioio;

    private float pulseDuration_secs;
    private float frequency_hz;
    private Thread workerThread;
    private Timer timer;

    public PulseSignalDevice(IOIO ioio, int pinNum, PulseInput.PulseMode pulseMode, boolean isDoublePrecision) throws
            ConnectionLostException {
        this.ioio = ioio;
//        this.pulseInput = ioio.openPulseInput(pinNum, pulseMode);
        if (isDoublePrecision) {
            this.pulseInput = ioio.openPulseInput(new DigitalInput.Spec(pinNum), PulseInput.ClockRate.RATE_250KHz,
                    pulseMode, true);
        } else {
            this.pulseInput = ioio.openPulseInput(new DigitalInput.Spec(pinNum), PulseInput.ClockRate.RATE_250KHz,
                    pulseMode, false);
        }
        this.timer = new Timer();
    }

    public PulseSignalDevice(IOIO ioio, int pinNum, int frequencyHz) throws ConnectionLostException {
        this.ioio = ioio;
        this.pwmOutput = ioio.openPwmOutput(pinNum, frequencyHz);
    }

    public PulseInput getPulseInput() {
        return this.pulseInput;
    }

    public void setPulseInput(PulseInput pulseInput) {
        this.pulseInput = pulseInput;
    }

    public PwmOutput getPwmOutput() {
        return pwmOutput;
    }

    public void setPwmOutput(PwmOutput pwmOutput) {
        this.pwmOutput = pwmOutput;
    }

    public IOIO getIoio() {
        return this.ioio;
    }

    public void setIoio(IOIO ioio) {
        this.ioio = ioio;
    }

    public float getPulseDuration() throws ConnectionLostException, InterruptedException {
        return this.pulseInput.getDuration();
    }

    public float getPulseDurationInterruptible() throws ConnectionLostException, InterruptedException {
        /* Gotta use PulseInput.getDurationBuffered() instead of PulseInput.getFrequency(). Because getFrequency()
         * blocks IOIO execution and can't be interrupted. That's the right way to calculate the frequency
         * with a timeout, if that timeout occurs then we assume zero hertz of frequency.
         * Reference: https://groups.google.com/forum/#!topic/ioio-users/2prtUQMQ-Wc
         */
        this.workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int waitTime_millisecs = 38;
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        workerThread.interrupt();
                    }
                };

                timer.schedule(timerTask, waitTime_millisecs);

                try {
                    pulseDuration_secs = pulseInput.getDurationBuffered();
                    timerTask.cancel();
                } catch (InterruptedException | ConnectionLostException ex) {
                    pulseDuration_secs = 0.0375f;
                }
            }
        });

        this.workerThread.setName("GetDurationInterruptibleThread");
        this.workerThread.start();

        return this.pulseDuration_secs;

    }

    public float getFrequency() throws ConnectionLostException, InterruptedException {
        /* Gotta use PulseInput.getDurationBuffered() instead of PulseInput.getFrequency(). Because getFrequency()
         * blocks IOIO execution and can't be interrupted. That's the right way to calculate the frequency
         * with a timeout, if that timeout occurs then we assume zero hertz of frequency.
         * Reference: https://groups.google.com/forum/#!topic/ioio-users/2prtUQMQ-Wc
         */
        this.workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int waitTime_millisecs = 1;
//                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        workerThread.interrupt();
                    }
                };
                timer.schedule(timerTask, waitTime_millisecs);

                try {
                    frequency_hz = 1.0f / pulseInput.getDurationBuffered();
                    timerTask.cancel();
                } catch (InterruptedException | ConnectionLostException ex) {
                    frequency_hz = 0;
//                } finally {
//                    timer.cancel();
//                    timer.purge();
                }
            }
        });

        this.workerThread.setName("GetFrequencyThread");
        this.workerThread.start();

        return this.frequency_hz;
    }
}
