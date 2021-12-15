package bgu.spl.mics.application.objects;

public class CpuPair implements Comparable {
    private CPU cpu;
    private long ticks;

    public CpuPair(CPU _cpu,long _ticks){
        cpu = _cpu;
        ticks = _ticks;
    }
    @Override
    public int compareTo(Object other) {
        if(this.ticks > ((CpuPair)other).ticks)
            return 1;
        else
            if(this.ticks < ((CpuPair)other).ticks)
                return -1;
        return 0;
    }

    public CPU getCpu() {
        return cpu;
    }

    public long getTicks() {
        return ticks;
    }

    public void setTicks(long ticks) {
        this.ticks = ticks;
    }
}
