/*
 * Copyright (c) 2014 Funky Android Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.funkyandroid.sync;

import java.io.IOException;

public final class Main {

    public static void main(String[] args) {
        if(args.length < 2) {
            showUsage();
            System.exit(-1);
        }

        Configuration configuration = parseCommandLine(args);
        System.out.println("Synchronising "+configuration.source+" to "+configuration.destination);
        if(configuration.deleteAfterSync) {
            System.out.println("Deleting source files after synchronisation");
        }

        try (Destination destination = getDestination(configuration.destination)) {
            Synchronizer synchronizer = new Synchronizer(destination);
            try(Source source = getSource(configuration.source)) {
                source.synchronizeUsing(synchronizer, configuration.deleteAfterSync);
            } catch(IOException e) {
                e.printStackTrace();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static Configuration parseCommandLine(String[] args) {
        boolean hasSwitches = args.length > 2;

        int locationsStart;
        if(hasSwitches) {
            locationsStart = args.length-2;
        } else {
            locationsStart = 0;
        }

        Configuration configuration = new Configuration();
        configuration.source = args[locationsStart];
        configuration.destination = args[locationsStart+1];

        for(int i = 0 ; i < locationsStart ; i++) {
            parseConfigurationSwitch(configuration, args[i]);
        }

        return configuration;
    }

    private static void parseConfigurationSwitch(final Configuration configuration, final String cliSwitch) {
        int position = 0;
        while(position < cliSwitch.length()) {
            char switchFlag = cliSwitch.charAt(position);
            switch(switchFlag) {
                case '-':
                    break;
                case 'd':
                    configuration.deleteAfterSync = true;
                    break;
                default:
                    System.err.println("Unknown switch "+switchFlag);
                    break;
            }
            position++;
        }
    }

    private static Destination getDestination(final String description)
            throws IOException {
        DestinationFactory destinationFactory = new DestinationFactory();
        return destinationFactory.getDestinationFor(description);
    }

    private static Source getSource(final String description)
            throws IOException {
        SourceFactory sourceFactory = new SourceFactory();
        return sourceFactory.getSourceFor(description);
    }

    private static void showUsage() {
        System.err.println("Usage : cf-rsync [-d] from to");
        System.err.println();
        System.err.println("where from and to are of the form;");
        System.err.println("cloudfiles://[[username:apikey@]region]/bucket_name or local_directory_name");
        System.err.println();
        System.err.println("The available switches are;");
        System.err.println("-d - Delete the file from the source after it has been copied to the destination.");
        System.err.println();
    }

    private static class Configuration {
        boolean deleteAfterSync = false;
        String source;
        String destination;
    }
}
