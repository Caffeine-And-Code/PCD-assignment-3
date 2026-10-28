package main

import (
	"fmt"
	"math/rand"
)

type MatchResult struct {
	playerA int
	playerB int
	winner  int
}

func Player(pid int, m int, players []chan int, matchResults []chan MatchResult, done chan int) {

	fmt.Printf("Hi from Player %d\n", pid)

	player_len := 1 << m

	opponent_id := pid
	if pid%2 == 0 {
		opponent_id = pid + 1
	} else {
		opponent_id = pid - 1
	}

	match_num := player_len / 2

	for round := range m {
		choose := rand.Intn(2)
		number := rand.Intn(6)
		opponent_choose := 1 - choose
		opponent_number := 0
		player_a := 0
		player_b := 0

		if pid < opponent_id {
			player_a = pid
			player_b = opponent_id
			fmt.Printf("[Player %d] -> Inizio match player %d vs player %d\n", pid, pid, opponent_id)
			players[opponent_id] <- choose
			opponent_choose = <-players[pid]
		} else {
			player_a = opponent_id
			player_b = pid
			opponent_choose = <-players[pid]
			choose = 1 - opponent_choose
			players[opponent_id] <- choose
		}

		fmt.Printf("[Player %d] -> Il player %d ha scelto %d\n", pid, opponent_id, opponent_choose)

		if pid < opponent_id {
			players[opponent_id] <- number
			opponent_number = <-players[pid]
		} else {
			opponent_number = <-players[pid]
			players[opponent_id] <- number
		}

		isOdd := (number+opponent_number)%2 == 0

		if (choose == 0 && isOdd == true) ||
			(choose == 1 && isOdd == false) {

			fmt.Printf("[Player %d] -> Ho vinto!\n", pid)

			result := MatchResult{
				playerA: player_a,
				playerB: player_b,
				winner:  pid,
			}

			for i := range player_len {
				if i != pid {
					matchResults[i] <- result
				}
			}

			if round == m-1 {
				done <- pid
				return
			}

			roundResults := make([]MatchResult, 0, match_num-1)

			for i := 0; i < match_num-1; i++ {
				match_result := <-matchResults[pid]
				roundResults = append(roundResults, match_result)
			}

			group_size := 1 << (round + 2)
			half_group_size := group_size / 2
			other_group_start := (pid / group_size) * group_size
			if pid < other_group_start+half_group_size {
				other_group_start = other_group_start + half_group_size
			}

			for _, result := range roundResults {
				if result.playerA >= other_group_start && result.playerA < other_group_start+half_group_size {
					opponent_id = result.winner
					fmt.Printf("[Player %d] -> Prossimo avversario: Player %d\n", pid, opponent_id)
					break
				}
			}

			match_num = match_num / 2

		} else {
			fmt.Printf("[Player %d] -> Ho perso!\n", pid)
			break
		}
	}
}

func main() {
	m := 3
	player_len := 1 << m

	players := make([]chan int, player_len)
	matchResults := make([]chan MatchResult, player_len)

	done := make(chan int)

	for i := range player_len {
		players[i] = make(chan int)
		matchResults[i] = make(chan MatchResult, player_len*m)
	}

	for i := range player_len {
		go Player(i, m, players, matchResults, done)
	}

	winner := <-done

	fmt.Printf("\nTOURNAMENT WINNER: Player %d\n", winner)
}
