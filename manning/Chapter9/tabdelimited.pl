#!perl -w

my @vec = ();

while( <> ) {
  chop;

  @words = split( /(?<!\\)\t/ );

  push @vec, [ @words ];
}

my $i=0;
foreach $words (@vec) {
  print "Record $i\n";
  $i++;
  foreach $word (@$words) {
    print "  $word\n";
  }
}
